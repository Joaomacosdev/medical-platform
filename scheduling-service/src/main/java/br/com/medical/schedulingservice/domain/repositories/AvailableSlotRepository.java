package br.com.medical.schedulingservice.domain.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;

public interface AvailableSlotRepository {

    List<AvailableSlot> buscarDisponiveisPorProfissionalEData(Long profissionalId, LocalDate data);

    Optional<AvailableSlot> buscarPorProfissionalEDataHora(Long profissionalId, LocalDateTime dataHora);

    AvailableSlot salvar(AvailableSlot slot);
}