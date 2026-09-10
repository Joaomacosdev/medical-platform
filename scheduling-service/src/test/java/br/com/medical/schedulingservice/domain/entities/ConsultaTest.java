package br.com.medical.schedulingservice.domain.entities;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultaTest {

    @Test
    void deveCancelarConsultaAgendada() {
        Consulta consulta = Consulta.builder().status(ConsultaStatus.AGENDADA).build();

        consulta.cancelar();

        assertThat(consulta.getStatus()).isEqualTo(ConsultaStatus.CANCELADA);
    }

    @Test
    void naoDeveCancelarConsultaJaCancelada() {
        Consulta consulta = Consulta.builder().status(ConsultaStatus.CANCELADA).build();

        assertThatThrownBy(consulta::cancelar).isInstanceOf(ConsultaInvalidaException.class);
    }

    @Test
    void naoDeveCancelarConsultaRealizada() {
        Consulta consulta = Consulta.builder().status(ConsultaStatus.REALIZADA).build();

        assertThatThrownBy(consulta::cancelar).isInstanceOf(ConsultaInvalidaException.class);
    }

    @Test
    void deveValidarQueDataDaConsultaEstaNoFuturo() {
        Consulta consulta = Consulta.builder().dataConsulta(LocalDateTime.now().minusDays(1)).build();

        assertThatThrownBy(consulta::validarAgendamentoFuturo).isInstanceOf(ConsultaInvalidaException.class);
    }

    @Test
    void devePermitirDataDaConsultaNoFuturo() {
        Consulta consulta = Consulta.builder().dataConsulta(LocalDateTime.now().plusDays(1)).build();

        assertThatCode(consulta::validarAgendamentoFuturo).doesNotThrowAnyException();
    }

    @Test
    void devePertencerAoPacienteCorreto() {
        Consulta consulta = Consulta.builder().pacienteId(1L).build();

        assertThat(consulta.pertenceAoPaciente(1L)).isTrue();
        assertThat(consulta.pertenceAoPaciente(2L)).isFalse();
    }
}