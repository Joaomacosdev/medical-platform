package br.com.medical.schedulingservice.domain.usecases;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;

public record EditarConsultaComando(
        Long consultaId,
        LocalDateTime dataConsulta,
        ConsultaTipo tipo,
        ConsultaStatus status,
        String observacoes,
        Long solicitanteId,
        UserRole solicitanteRole
) {
}