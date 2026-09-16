package br.com.medical.schedulingservice.domain.entities;

import java.time.LocalDateTime;

import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
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
public class Consulta {

    private Long id;
    private Long pacienteId;
    private Long profissionalId;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataConsulta;
    private ConsultaTipo tipo;
    private ConsultaStatus status;
    private String observacoes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void validarAgendamentoFuturo() {
        if (dataConsulta == null || dataConsulta.isBefore(LocalDateTime.now())) {
            throw new ConsultaInvalidaException("A data da consulta deve ser no futuro.");
        }
    }

    public void cancelar() {
        if (this.status == ConsultaStatus.CANCELADA) {
            throw new ConsultaInvalidaException("A consulta ja esta cancelada.");
        }
        if (this.status == ConsultaStatus.REALIZADA) {
            throw new ConsultaInvalidaException("Nao e possivel cancelar uma consulta ja realizada.");
        }
        this.status = ConsultaStatus.CANCELADA;
    }

    public boolean pertenceAoPaciente(Long usuarioId) {
        return this.pacienteId != null && this.pacienteId.equals(usuarioId);
    }
}