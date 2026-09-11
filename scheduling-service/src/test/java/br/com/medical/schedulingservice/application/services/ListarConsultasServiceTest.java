package br.com.medical.schedulingservice.application.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasComando;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarConsultasServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @InjectMocks
    private ListarConsultasService service;

    @Test
    void pacienteDeveTerFiltroForcadoParaSuasProprias() {
        var filtroOriginal = new ConsultaFiltro(999L, null, null, null);
        var comando = new ListarConsultasComando(filtroOriginal, 5L, UserRole.PACIENTE);
        when(consultaRepository.buscarComFiltros(any())).thenReturn(List.of());

        service.listar(comando);

        ArgumentCaptor<ConsultaFiltro> captor = ArgumentCaptor.forClass(ConsultaFiltro.class);
        verify(consultaRepository).buscarComFiltros(captor.capture());
        assertThat(captor.getValue().pacienteId()).isEqualTo(5L);
    }

    @Test
    void medicoDeveUsarFiltroInformadoSemRestricao() {
        var filtroOriginal = new ConsultaFiltro(999L, 7L, null, null);
        var comando = new ListarConsultasComando(filtroOriginal, 20L, UserRole.MEDICO);
        when(consultaRepository.buscarComFiltros(any())).thenReturn(List.of());

        service.listar(comando);

        ArgumentCaptor<ConsultaFiltro> captor = ArgumentCaptor.forClass(ConsultaFiltro.class);
        verify(consultaRepository).buscarComFiltros(captor.capture());
        assertThat(captor.getValue()).isEqualTo(filtroOriginal);
    }

    @Test
    void deveLancarExcecaoQuandoConsultaNaoEncontrada() {
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(1L, 5L, UserRole.MEDICO))
                .isInstanceOf(ConsultaNotFoundException.class);
    }

    @Test
    void pacienteNaoPodeVerConsultaDeOutroPaciente() {
        Consulta consulta = Consulta.builder().id(1L).pacienteId(100L).build();
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> service.buscarPorId(1L, 5L, UserRole.PACIENTE))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void pacienteConsegueVerAProprioConsulta() {
        Consulta consulta = Consulta.builder().id(1L).pacienteId(5L).build();
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(consulta));

        Consulta resultado = service.buscarPorId(1L, 5L, UserRole.PACIENTE);

        assertThat(resultado.getId()).isEqualTo(1L);
    }
}