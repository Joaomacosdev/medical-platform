package br.com.medical.schedulingservice.application.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.events.AppointmentEventType;
import br.com.medical.schedulingservice.domain.events.AppointmentHistoryEvent;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.SlotIndisponivelException;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.domain.usecases.CriarConsultaUseCase;
import br.com.medical.schedulingservice.domain.usecases.NovaConsultaComando;
import br.com.medical.schedulingservice.frameworks.rabbitmq.AppointmentEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CriarConsultaService implements CriarConsultaUseCase {

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaEventPublisher consultaEventPublisher;
    private final AppointmentEventPublisherService appointmentEventPublisher;

    @Override
    @Transactional
    public Consulta criar(NovaConsultaComando comando) {
        if (comando.solicitanteRole() != UserRole.MEDICO && comando.solicitanteRole() != UserRole.ENFERMEIRO) {
            throw new AcessoNegadoException("Apenas medicos e enfermeiros podem criar consultas.");
        }

        Usuario paciente = usuarioRepository.buscarPorId(comando.pacienteId())
                .orElseThrow(() -> new UsuarioNotFoundException(comando.pacienteId()));
        if (paciente.getRole() != UserRole.PACIENTE) {
            throw new ConsultaInvalidaException("O usuario informado como paciente nao possui o perfil PACIENTE.");
        }

        Usuario profissional = usuarioRepository.buscarPorId(comando.profissionalId())
                .orElseThrow(() -> new UsuarioNotFoundException(comando.profissionalId()));
        if (!profissional.isProfissionalDeSaude()) {
            throw new ConsultaInvalidaException("O usuario informado como profissional nao e medico nem enfermeiro.");
        }

        Consulta consulta = Consulta.builder()
                .pacienteId(paciente.getId())
                .profissionalId(profissional.getId())
                .dataSolicitacao(LocalDateTime.now())
                .dataConsulta(comando.dataConsulta())
                .tipo(comando.tipo())
                .status(ConsultaStatus.AGENDADA)
                .observacoes(comando.observacoes())
                .build();

        consulta.validarAgendamentoFuturo();

        if (consultaRepository.existeConflitoDeHorario(profissional.getId(), consulta.getDataConsulta(), null)) {
            throw new SlotIndisponivelException("O profissional ja possui uma consulta agendada neste horario.");
        }

        Consulta salva = consultaRepository.salvar(consulta);
        log.info("Consulta {} criada para paciente {} com profissional {}", salva.getId(), salva.getPacienteId(), salva.getProfissionalId());

        consultaEventPublisher.publicarConsultaCriada(salva);
        appointmentEventPublisher.publishEvent(new AppointmentHistoryEvent(
            AppointmentEventType.APPOINTMENT_CREATED,
            salva.getId(),
            salva.getPacienteId(),
            salva.getProfissionalId(),
            salva.getDataConsulta(),
            salva.getStatus().name()));

        return salva;
    }
}