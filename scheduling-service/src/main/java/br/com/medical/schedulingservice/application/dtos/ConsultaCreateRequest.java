package br.com.medical.schedulingservice.application.dtos;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConsultaCreateRequest(
        @NotNull(message = "pacienteId e obrigatorio") Long pacienteId,
        @NotNull(message = "profissionalId e obrigatorio") Long profissionalId,
        @NotNull(message = "dataConsulta e obrigatorio") @Future(message = "dataConsulta deve ser no futuro") LocalDateTime dataConsulta,
        @NotNull(message = "tipo e obrigatorio") ConsultaTipo tipo,
        @Size(max = 1000, message = "observacoes deve ter no maximo 1000 caracteres") String observacoes
) {
}