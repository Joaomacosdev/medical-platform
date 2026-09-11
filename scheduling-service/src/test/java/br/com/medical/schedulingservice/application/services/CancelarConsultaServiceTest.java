package br.com.medical.schedulingservice.application.services;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaComando;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private CancelarConsultaService service;

    @Test
    void deveCancelarConsultaComSucesso() {
        Consulta consulta = Consulta.builder().id(1L).status(ConsultaStatus.AGENDADA).build();
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.salvar(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        var comando = new CancelarConsultaComando(1L, 20L, UserRole.MEDICO);
        Consulta resultado = service.cancelar(comando);

        assertThat(resultado.getStatus()).isEqualTo(ConsultaStatus.CANCELADA);
    }

    @Test
    void deveNegarAcessoQuandoSolicitanteForPaciente() {
        var comando = new CancelarConsultaComando(1L, 5L, UserRole.PACIENTE);

        assertThatThrownBy(() -> service.cancelar(comando)).isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoConsultaNaoEncontrada() {
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.empty());
        var comando = new CancelarConsultaComando(1L, 20L, UserRole.ENFERMEIRO);

        assertThatThrownBy(() -> service.cancelar(comando)).isInstanceOf(ConsultaNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoAoCancelarConsultaJaCancelada() {
        Consulta consulta = Consulta.builder().id(1L).status(ConsultaStatus.CANCELADA).build();
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(consulta));
        var comando = new CancelarConsultaComando(1L, 20L, UserRole.MEDICO);

        assertThatThrownBy(() -> service.cancelar(comando)).isInstanceOf(ConsultaInvalidaException.class);
    }
}