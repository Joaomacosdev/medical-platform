package br.com.medical.schedulingservice.application.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.AvailableSlotRepository;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarDisponibilidadeServiceTest {

    @Mock
    private AvailableSlotRepository availableSlotRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConsultarDisponibilidadeService service;

    @Test
    void deveRetornarSlotsDisponiveis() {
        Usuario medico = Usuario.builder().id(2L).role(UserRole.MEDICO).build();
        LocalDate data = LocalDate.now().plusDays(1);
        AvailableSlot slot = AvailableSlot.builder().id(1L).profissionalId(2L).dataHora(LocalDateTime.now()).disponivel(true).build();

        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(medico));
        when(availableSlotRepository.buscarDisponiveisPorProfissionalEData(2L, data)).thenReturn(List.of(slot));

        List<AvailableSlot> resultado = service.consultar(2L, data);

        assertThat(resultado).containsExactly(slot);
    }

    @Test
    void deveLancarExcecaoQuandoProfissionalNaoEncontrado() {
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultar(2L, LocalDate.now()))
                .isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForProfissionalDeSaude() {
        Usuario paciente = Usuario.builder().id(2L).role(UserRole.PACIENTE).build();
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(paciente));

        assertThatThrownBy(() -> service.consultar(2L, LocalDate.now()))
                .isInstanceOf(ConsultaInvalidaException.class);
    }
}