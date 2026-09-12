package br.com.medical.schedulingservice.frameworks.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.frameworks.rabbitmq.events.ReservationNotificationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPublisherService implements ConsultaEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.rabbitmq.exchange.medical-exchange}")
    private String medicalExchange;

    @Override
    public void publicarConsultaCriada(Consulta consulta) {
        publicar(consulta);
    }

    @Override
    public void publicarConsultaEditada(Consulta consulta) {
        publicar(consulta);
    }

    private void publicar(Consulta consulta) {
        Usuario paciente = usuarioRepository.buscarPorId(consulta.getPacienteId())
                .orElseThrow(() -> new UsuarioNotFoundException(consulta.getPacienteId()));
        Usuario profissional = usuarioRepository.buscarPorId(consulta.getProfissionalId())
                .orElseThrow(() -> new UsuarioNotFoundException(consulta.getProfissionalId()));

        var consultation = new ReservationNotificationPayload.ConsultationDetails(
                consulta.getId(),
                profissional.getNome(),
                profissional.getEspecialidade(),
                consulta.getDataConsulta()
        );
        var payload = new ReservationNotificationPayload(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getTelefone(),
                consultation
        );

        log.info("Publicando payload: patientId={}, patientName={}, patientEmail={}, doctor={}",
                payload.patientId(), payload.patientName(), payload.patientEmail(),
                payload.consultation().doctorName());
        rabbitTemplate.convertAndSend(medicalExchange, "", payload);
    }
}