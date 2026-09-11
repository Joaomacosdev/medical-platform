package br.com.medical.schedulingservice.domain.repositories;

import java.time.LocalDate;

import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;

public record ConsultaFiltro(
        Long pacienteId,
        Long profissionalId,
        ConsultaStatus status,
        LocalDate data
) {
}