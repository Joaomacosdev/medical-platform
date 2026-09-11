package br.com.medical.schedulingservice.interface_adapters.graphql;

import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;

public record ConsultaFiltroInput(
        Long pacienteId,
        Long profissionalId,
        ConsultaStatus status,
        String data
) {
}