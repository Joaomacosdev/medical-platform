package br.com.medical.schedulingservice.frameworks.rabbitmq;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.frameworks.rabbitmq.events.ReservationNotificationPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationPublisherServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservationPublisherService service;

    @Test
    void devePublicarNotificacaoComDadosDoPacienteEDoProfissionalNaCriacao() {
        ReflectionTestUtils.setField(service, "medicalExchange", "medical_exchange");

        Usuario paciente = Usuario.builder().id(3L).nome("Joao Lima").email("paciente@hospital.com")
                .telefone("4002-8922").role(UserRole.PACIENTE).build();
        Usuario medico = Usuario.builder().id(1L).nome("Dr. Ricardo Silva").especialidade("Cardiologia")
                .role(UserRole.MEDICO).build();
        LocalDateTime dataConsulta = LocalDateTime.now().plusDays(1);
        Consulta consulta = Consulta.builder().id(10L).pacienteId(3L).profissionalId(1L)
                .dataConsulta(dataConsulta).tipo(ConsultaTipo.PRESENCIAL).status(ConsultaStatus.AGENDADA).build();

        when(usuarioRepository.buscarPorId(3L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(medico));

        service.publicarConsultaCriada(consulta);

        ArgumentCaptor<ReservationNotificationPayload> captor = ArgumentCaptor.forClass(ReservationNotificationPayload.class);
        verify(rabbitTemplate).convertAndSend(eq("medical_exchange"), eq(""), captor.capture());

        ReservationNotificationPayload payload = captor.getValue();
        assertThat(payload.patientId()).isEqualTo(3L);
        assertThat(payload.patientName()).isEqualTo("Joao Lima");
        assertThat(payload.patientEmail()).isEqualTo("paciente@hospital.com");
        assertThat(payload.patientNumber()).isEqualTo("4002-8922");
        assertThat(payload.consultation().consultationId()).isEqualTo(10L);
        assertThat(payload.consultation().doctorName()).isEqualTo("Dr. Ricardo Silva");
        assertThat(payload.consultation().medicalSpecialty()).isEqualTo("Cardiologia");
        assertThat(payload.consultation().consultationDate()).isEqualTo(dataConsulta);
    }

    @Test
    void devePublicarNotificacaoAoEditarConsulta() {
        ReflectionTestUtils.setField(service, "medicalExchange", "medical_exchange");

        Usuario paciente = Usuario.builder().id(3L).nome("Joao Lima").email("paciente@hospital.com").role(UserRole.PACIENTE).build();
        Usuario medico = Usuario.builder().id(1L).nome("Dr. Ricardo Silva").role(UserRole.MEDICO).build();
        Consulta consulta = Consulta.builder().id(10L).pacienteId(3L).profissionalId(1L)
                .dataConsulta(LocalDateTime.now().plusDays(2)).status(ConsultaStatus.CONFIRMADA).build();

        when(usuarioRepository.buscarPorId(3L)).thenReturn(Optional.of(paciente));
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(medico));

        service.publicarConsultaEditada(consulta);

        verify(rabbitTemplate).convertAndSend(eq("medical_exchange"), eq(""), org.mockito.ArgumentMatchers.any(ReservationNotificationPayload.class));
    }
}