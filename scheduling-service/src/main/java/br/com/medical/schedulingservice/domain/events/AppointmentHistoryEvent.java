package br.com.medical.schedulingservice.domain.events;

import java.time.LocalDateTime;

public record AppointmentHistoryEvent(
        AppointmentEventType eventType,
        Long appointmentId,
        Long patientId,
        Long doctorId,
        LocalDateTime scheduledAt,
        String status,
        LocalDateTime oldScheduledAt,
        LocalDateTime timestamp
) {

    public AppointmentHistoryEvent(
            AppointmentEventType eventType,
            Long appointmentId,
            Long patientId,
            Long doctorId,
            LocalDateTime scheduledAt,
            String status) {
        this(eventType, appointmentId, patientId, doctorId, scheduledAt, status, null, LocalDateTime.now());
    }
}