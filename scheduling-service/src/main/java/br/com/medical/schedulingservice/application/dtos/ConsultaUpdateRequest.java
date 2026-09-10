package br.com.medical.schedulingservice.application.dtos;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

public record ConsultaUpdateRequest(
        @Future(message = "dataConsulta deve ser no futuro") LocalDateTime dataConsulta,
        ConsultaTipo tipo,
        ConsultaStatus status,
        @Size(max = 1000, message = "observacoes deve ter no maximo 1000 caracteres") String observacoes
) {
}