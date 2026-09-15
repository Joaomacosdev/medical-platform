package br.com.medical.schedulingservice.frameworks.rabbitmq;

import br.com.medical.schedulingservice.domain.events.AppointmentHistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentEventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange.appointment-events:appointment.events}")
    private String appointmentExchange;

    public void publishEvent(AppointmentHistoryEvent event) {
        String routingKey = event.eventType().name().toLowerCase().replace('_', '.');
        log.info("Publishing appointment history event: type={}, appointmentId={}", event.eventType(), event.appointmentId());
        rabbitTemplate.convertAndSend(appointmentExchange, routingKey, event);
    }
}