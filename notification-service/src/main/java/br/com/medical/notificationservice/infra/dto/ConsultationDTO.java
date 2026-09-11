package br.com.medical.notificationservice.infra.dto;

import java.time.LocalDateTime;

public record ConsultationDTO (
        Long consultationId,
        String doctorName,
        String medicalSpecialty,
        LocalDateTime consultationDate
){}
