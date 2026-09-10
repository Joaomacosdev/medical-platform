package br.com.medical.schedulingservice.application.dtos;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        Long profissionalId,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataConsulta,
        ConsultaTipo tipo,
        ConsultaStatus status,
        String observacoes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}