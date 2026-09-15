package br.com.medical.schedulingservice.application.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.events.ConsultaEventPublisher;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaInvalidaException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.exceptions.SlotIndisponivelException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.EditarConsultaComando;
import br.com.medical.schedulingservice.frameworks.rabbitmq.AppointmentEventPublisherService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private ConsultaEventPublisher consultaEventPublisher;
    @Mock
    private AppointmentEventPublisherService appointmentEventPublisher;

    @InjectMocks
    private EditarConsultaService service;

    private Consulta consultaExistente(ConsultaStatus status, LocalDateTime dataConsulta) {
        return Consulta.builder()
                .id(1L)
                .pacienteId(10L)
                .profissionalId(20L)
                .dataSolicitacao(LocalDateTime.now().minusDays(1))
                .dataConsulta(dataConsulta)
                .tipo(ConsultaTipo.PRESENCIAL)
                .status(status)
                .build();
    }

    @Test
    void deveEditarConsultaComSucesso() {
        LocalDateTime novaData = LocalDateTime.now().plusDays(2);
        Consulta existente = consultaExistente(ConsultaStatus.AGENDADA, LocalDateTime.now().plusDays(1));
        var comando = new EditarConsultaComando(1L, novaData, ConsultaTipo.RETORNO, null, "nova obs", 20L, UserRole.MEDICO);

        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(consultaRepository.existeConflitoDeHorario(20L, novaData, 1L)).thenReturn(false);
        when(consultaRepository.salvar(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        Consulta resultado = service.editar(comando);

        assertThat(resultado.getDataConsulta()).isEqualTo(novaData);
        assertThat(resultado.getTipo()).isEqualTo(ConsultaTipo.RETORNO);
        assertThat(resultado.getObservacoes()).isEqualTo("nova obs");
        verify(consultaEventPublisher).publicarConsultaEditada(resultado);
        verify(appointmentEventPublisher).publishEvent(any());
    }

    @Test
    void deveNegarAcessoQuandoSolicitanteForPaciente() {
        var comando = new EditarConsultaComando(1L, null, null, null, null, 10L, UserRole.PACIENTE);

        assertThatThrownBy(() -> service.editar(comando)).isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoConsultaNaoEncontrada() {
        var comando = new EditarConsultaComando(1L, null, null, null, null, 20L, UserRole.MEDICO);
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editar(comando)).isInstanceOf(ConsultaNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoAoEditarConsultaCancelada() {
        Consulta existente = consultaExistente(ConsultaStatus.CANCELADA, LocalDateTime.now().plusDays(1));
        var comando = new EditarConsultaComando(1L, null, null, null, "obs", 20L, UserRole.MEDICO);
        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.editar(comando)).isInstanceOf(ConsultaInvalidaException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNovoHorarioIndisponivel() {
        LocalDateTime novaData = LocalDateTime.now().plusDays(3);
        Consulta existente = consultaExistente(ConsultaStatus.AGENDADA, LocalDateTime.now().plusDays(1));
        var comando = new EditarConsultaComando(1L, novaData, null, null, null, 20L, UserRole.MEDICO);

        when(consultaRepository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(consultaRepository.existeConflitoDeHorario(anyLong(), any(), anyLong())).thenReturn(true);

        assertThatThrownBy(() -> service.editar(comando)).isInstanceOf(SlotIndisponivelException.class);
    }
}