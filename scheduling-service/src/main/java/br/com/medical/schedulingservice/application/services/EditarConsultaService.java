package br.com.medical.schedulingservice.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.events.AppointmentEventType;
import br.com.medical.schedulingservice.domain.events.AppointmentHistoryEvent;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.exceptions.SlotIndisponivelException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.EditarConsultaComando;
import br.com.medical.schedulingservice.domain.usecases.EditarConsultaUseCase;
import br.com.medical.schedulingservice.frameworks.rabbitmq.AppointmentEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditarConsultaService implements EditarConsultaUseCase {

    private final ConsultaRepository consultaRepository;
    private final ConsultaEventPublisher consultaEventPublisher;
    private final AppointmentEventPublisherService appointmentEventPublisher;

    @Override
    @Transactional
    public Consulta editar(EditarConsultaComando comando) {
        if (comando.solicitanteRole() != UserRole.MEDICO && comando.solicitanteRole() != UserRole.ENFERMEIRO) {
            throw new AcessoNegadoException("Apenas medicos e enfermeiros podem editar consultas.");
        }

        Consulta consulta = consultaRepository.buscarPorId(comando.consultaId())
                .orElseThrow(() -> new ConsultaNotFoundException(comando.consultaId()));
        LocalDateTime oldScheduledAt = consulta.getDataConsulta();

        if (consulta.getStatus() == ConsultaStatus.CANCELADA || consulta.getStatus() == ConsultaStatus.REALIZADA) {
            throw new ConsultaInvalidaException("Nao e possivel editar uma consulta cancelada ou ja realizada.");
        }

        if (comando.dataConsulta() != null && !comando.dataConsulta().equals(consulta.getDataConsulta())) {
            if (consultaRepository.existeConflitoDeHorario(consulta.getProfissionalId(), comando.dataConsulta(), consulta.getId())) {
                throw new SlotIndisponivelException("O profissional ja possui uma consulta agendada neste horario.");
            }
            consulta.setDataConsulta(comando.dataConsulta());
            consulta.validarAgendamentoFuturo();
        }
        if (comando.tipo() != null) {
            consulta.setTipo(comando.tipo());
        }
        if (comando.status() != null) {
            consulta.setStatus(comando.status());
        }
        if (comando.observacoes() != null) {
            consulta.setObservacoes(comando.observacoes());
        }

        Consulta atualizada = consultaRepository.salvar(consulta);
        log.info("Consulta {} editada por usuario {}", atualizada.getId(), comando.solicitanteId());

        consultaEventPublisher.publicarConsultaEditada(atualizada);
        appointmentEventPublisher.publishEvent(new AppointmentHistoryEvent(
            AppointmentEventType.APPOINTMENT_RESCHEDULED,
            atualizada.getId(),
            atualizada.getPacienteId(),
            atualizada.getProfissionalId(),
            atualizada.getDataConsulta(),
            atualizada.getStatus().name(),
            oldScheduledAt,
            LocalDateTime.now()));

        return atualizada;
    }
}