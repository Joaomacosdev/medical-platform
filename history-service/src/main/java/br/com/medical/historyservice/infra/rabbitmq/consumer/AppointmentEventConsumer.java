package br.com.medical.historyservice.infra.rabbitmq.consumer;

import br.com.medical.historyservice.infra.persistence.entity.AppointmentEventEntity;
import br.com.medical.historyservice.infra.persistence.repository.AppointmentEventJpaRepository;
import br.com.medical.historyservice.infra.rabbitmq.event.AppointmentHistoryEventMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class AppointmentEventConsumer {

    private final AppointmentEventJpaRepository repository;

    public AppointmentEventConsumer(AppointmentEventJpaRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.appointment-events:history.appointment.events}")
    public void consume(AppointmentHistoryEventMessage event) {
        repository.save(new AppointmentEventEntity(
                UUID.randomUUID().toString(),
                String.valueOf(event.appointmentId()),
                String.valueOf(event.patientId()),
                event.doctorId() == null ? null : String.valueOf(event.doctorId()),
                event.scheduledAt().atOffset(ZoneOffset.UTC),
                event.status()));
    }
}