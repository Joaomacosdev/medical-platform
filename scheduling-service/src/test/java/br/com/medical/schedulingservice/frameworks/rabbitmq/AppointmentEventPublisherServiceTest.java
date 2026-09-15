package br.com.medical.schedulingservice.frameworks.rabbitmq;

import br.com.medical.schedulingservice.domain.events.AppointmentEventType;
import br.com.medical.schedulingservice.domain.events.AppointmentHistoryEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppointmentEventPublisherServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AppointmentEventPublisherService service;

    @Test
    void shouldPublishAppointmentHistoryEventWithDerivedRoutingKey() {
        ReflectionTestUtils.setField(service, "appointmentExchange", "appointment.events");
        AppointmentHistoryEvent event = new AppointmentHistoryEvent(
                AppointmentEventType.APPOINTMENT_CREATED,
                10L,
                20L,
                30L,
                LocalDateTime.of(2026, 9, 15, 10, 0),
                "AGENDADA");

        service.publishEvent(event);

        verify(rabbitTemplate).convertAndSend(eq("appointment.events"), eq("appointment.created"), eq(event));
    }
}