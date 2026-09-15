package br.com.medical.schedulingservice.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.events.AppointmentEventType;
import br.com.medical.schedulingservice.domain.events.AppointmentHistoryEvent;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaComando;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaUseCase;
import br.com.medical.schedulingservice.frameworks.rabbitmq.AppointmentEventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelarConsultaService implements CancelarConsultaUseCase {

    private final ConsultaRepository consultaRepository;
    private final ConsultaEventPublisher consultaEventPublisher;
    private final AppointmentEventPublisherService appointmentEventPublisher;

    @Override
    @Transactional
    public Consulta cancelar(CancelarConsultaComando comando) {
        if (comando.solicitanteRole() != UserRole.MEDICO && comando.solicitanteRole() != UserRole.ENFERMEIRO) {
            throw new AcessoNegadoException("Apenas medicos e enfermeiros podem cancelar consultas.");
        }

        Consulta consulta = consultaRepository.buscarPorId(comando.consultaId())
                .orElseThrow(() -> new ConsultaNotFoundException(comando.consultaId()));

        consulta.cancelar();
        Consulta cancelada = consultaRepository.salvar(consulta);
        log.info("Consulta {} cancelada por usuario {}", cancelada.getId(), comando.solicitanteId());
        consultaEventPublisher.publicarConsultaCancelada(cancelada);
        appointmentEventPublisher.publishEvent(new AppointmentHistoryEvent(
            AppointmentEventType.APPOINTMENT_CANCELLED,
            cancelada.getId(),
            cancelada.getPacienteId(),
            cancelada.getProfissionalId(),
            cancelada.getDataConsulta(),
            cancelada.getStatus().name()));

        return cancelada;
    }
}