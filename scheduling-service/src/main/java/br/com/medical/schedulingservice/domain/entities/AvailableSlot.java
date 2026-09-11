package br.com.medical.schedulingservice.domain.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlot {

    private Long id;
    private Long profissionalId;
    private LocalDateTime dataHora;
    private Integer duracaoMinutos;
    private boolean disponivel;
    private Long consultaId;
    private LocalDateTime createdAt;

    public void reservar(Long consultaId) {
        this.disponivel = false;
        this.consultaId = consultaId;
    }

    public void liberar() {
        this.disponivel = true;
        this.consultaId = null;
    }
}