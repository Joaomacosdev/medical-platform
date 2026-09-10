package br.com.medical.schedulingservice.application.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.SlotIndisponivelException;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.domain.usecases.NovaConsultaComando;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ConsultaEventPublisher consultaEventPublisher;

    @InjectMocks
    private CriarConsultaService service;

    private Usuario paciente;
    private Usuario medico;
    private LocalDateTime dataFutura;

    @BeforeEach
    void setUp() {
        paciente = Usuario.builder().id(1L).nome("Paciente").email("paciente@teste.com").role(UserRole.PACIENTE).build();
        medico = Usuario.builder().id(2L).nome("Dr. House").email("medico@teste.com").role(UserRole.MEDICO).build();
        dataFutura = LocalDateTime.now().plusDays(1);
    }

    @Test
    void deveCriarConsultaComSucesso() {
        var comando = new NovaConsultaComando(1L, 2L, dataFutura, ConsultaTipo.PRESENCIAL, "obs", 2L, UserRole.MEDICO);

        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(medico));
        when(consultaRepository.existeConflitoDeHorario(eq(2L), eq(dataFutura), eq((Long) null))).thenReturn(false);
        when(consultaRepository.salvar(any(Consulta.class))).thenAnswer(invocation -> {
            Consulta c = invocation.getArgument(0);
            c.setId(10L);
            return c;
        });

        Consulta resultado = service.criar(comando);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getStatus()).isEqualTo(ConsultaStatus.AGENDADA);
        assertThat(resultado.getPacienteId()).isEqualTo(1L);
        assertThat(resultado.getProfissionalId()).isEqualTo(2L);
        verify(consultaEventPublisher).publicarConsultaCriada(resultado);
    }

    @Test
    void deveNegarAcessoQuandoSolicitanteForPaciente() {
        var comando = new NovaConsultaComando(1L, 2L, dataFutura, ConsultaTipo.PRESENCIAL, null, 1L, UserRole.PACIENTE);

        assertThatThrownBy(() -> service.criar(comando)).isInstanceOf(AcessoNegadoException.class);
        verify(consultaRepository, never()).salvar(any());
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoEncontrado() {
        var comando = new NovaConsultaComando(99L, 2L, dataFutura, ConsultaTipo.PRESENCIAL, null, 2L, UserRole.MEDICO);
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(comando)).isInstanceOf(UsuarioNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInformadoNaoForPaciente() {
        var comando = new NovaConsultaComando(2L, 2L, dataFutura, ConsultaTipo.PRESENCIAL, null, 2L, UserRole.MEDICO);
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(medico));

        assertThatThrownBy(() -> service.criar(comando)).isInstanceOf(ConsultaInvalidaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoHorarioIndisponivel() {
        var comando = new NovaConsultaComando(1L, 2L, dataFutura, ConsultaTipo.PRESENCIAL, null, 2L, UserRole.MEDICO);
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(medico));
        when(consultaRepository.existeConflitoDeHorario(anyLong(), any(), eq((Long) null))).thenReturn(true);

        assertThatThrownBy(() -> service.criar(comando)).isInstanceOf(SlotIndisponivelException.class);
        verify(consultaRepository, never()).salvar(any());
    }
}