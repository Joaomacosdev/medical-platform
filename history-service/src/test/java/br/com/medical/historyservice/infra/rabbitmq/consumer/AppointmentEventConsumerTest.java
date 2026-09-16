package br.com.medical.historyservice.infra.rabbitmq.consumer;

import br.com.medical.historyservice.infra.persistence.repository.AppointmentEventJpaRepository;
import br.com.medical.historyservice.infra.rabbitmq.event.AppointmentEventType;
import br.com.medical.historyservice.infra.rabbitmq.event.AppointmentHistoryEventMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppointmentEventConsumerTest {

    @Autowired
    private AppointmentEventConsumer consumer;

    @Autowired
    private AppointmentEventJpaRepository repository;

    @Test
    void shouldPersistConsumedAppointmentEvent() {
        consumer.consume(new AppointmentHistoryEventMessage(
                AppointmentEventType.APPOINTMENT_CREATED,
                10L,
                20L,
                30L,
                LocalDateTime.of(2026, 9, 15, 10, 0),
                "AGENDADA",
                null,
                LocalDateTime.of(2026, 9, 15, 9, 0)));

        assertThat(repository.findByPatientId("20"))
                .singleElement()
                .satisfies(entity -> {
                    assertThat(entity.getAppointmentId()).isEqualTo("10");
                    assertThat(entity.getDoctorId()).isEqualTo("30");
                    assertThat(entity.getStatus()).isEqualTo("AGENDADA");
                });
    }
}