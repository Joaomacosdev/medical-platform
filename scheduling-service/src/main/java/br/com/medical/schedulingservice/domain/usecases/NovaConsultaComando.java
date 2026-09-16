package br.com.medical.schedulingservice.domain.usecases;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;

public record NovaConsultaComando(
        Long pacienteId,
        Long profissionalId,
        LocalDateTime dataConsulta,
        ConsultaTipo tipo,
        String observacoes,
        Long solicitanteId,
        UserRole solicitanteRole
) {
}