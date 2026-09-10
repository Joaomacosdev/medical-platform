package br.com.medical.schedulingservice.domain.usecases;

import br.com.medical.schedulingservice.domain.entities.UserRole;

public record CancelarConsultaComando(
        Long consultaId,
        Long solicitanteId,
        UserRole solicitanteRole
) {
}