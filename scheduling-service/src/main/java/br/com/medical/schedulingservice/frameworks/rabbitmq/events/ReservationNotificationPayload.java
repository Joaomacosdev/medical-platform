package br.com.medical.schedulingservice.frameworks.rabbitmq.events;

import java.time.LocalDateTime;

public record ReservationNotificationPayload(
        Long patientId,
        String patientName,
        String patientEmail,
        String patientNumber,
        ConsultationDetails consultation
) {

    public record ConsultationDetails(
            Long consultationId,
            String doctorName,
            String medicalSpecialty,
            LocalDateTime consultationDate
    ) {
    }
}