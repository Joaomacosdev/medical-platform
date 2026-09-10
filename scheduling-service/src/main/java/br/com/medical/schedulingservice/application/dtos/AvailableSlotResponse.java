package br.com.medical.schedulingservice.application.dtos;

import java.time.LocalDateTime;

public record AvailableSlotResponse(
        Long id,
        Long profissionalId,
        LocalDateTime dataHora,
        Integer duracaoMinutos,
        boolean disponivel
) {
}