package br.com.medical.schedulingservice.interface_adapters.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.medical.schedulingservice.application.dtos.AvailableSlotResponse;
import br.com.medical.schedulingservice.domain.usecases.ConsultarDisponibilidadeUseCase;
import br.com.medical.schedulingservice.interface_adapters.mappers.AvailableSlotMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Disponibilidade")
@RestController
@RequestMapping("/api/v1/disponibilidade")
@RequiredArgsConstructor
public class DisponibilidadeController {

    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    private final AvailableSlotMapper availableSlotMapper;

    @Operation(summary = "Lista os horarios disponiveis de um profissional em uma data")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    @GetMapping
    public ResponseEntity<List<AvailableSlotResponse>> consultar(@RequestParam Long profissionalId,
                                                                    @RequestParam LocalDate data) {
        var slots = consultarDisponibilidadeUseCase.consultar(profissionalId, data);
        return ResponseEntity.ok(availableSlotMapper.toResponseList(slots));
    }
}