package br.com.medical.schedulingservice.domain.usecases;

import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;

public record ListarConsultasComando(
        ConsultaFiltro filtro,
        Long solicitanteId,
        UserRole solicitanteRole
) {
}