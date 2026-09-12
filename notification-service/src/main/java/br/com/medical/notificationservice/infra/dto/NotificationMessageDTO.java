package br.com.medical.notificationservice.infra.dto;

public record NotificationMessageDTO(
        Long patientId,
        String patientName,
        String patientEmail,
        String patientNumber,
        ConsultationDTO consultation
) {}
