package br.com.medical.historyservice.infra.rabbitmq.event;

import java.time.LocalDateTime;

public record AppointmentHistoryEventMessage(
        AppointmentEventType eventType,
        Long appointmentId,
        Long patientId,
        Long doctorId,
        LocalDateTime scheduledAt,
        String status,
        LocalDateTime oldScheduledAt,
        LocalDateTime timestamp
) {
}