package br.com.medical.schedulingservice.domain.usecases;

import java.time.LocalDate;
import java.util.List;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;

public interface ConsultarDisponibilidadeUseCase {

    List<AvailableSlot> consultar(Long profissionalId, LocalDate data);
}