package br.com.medical.schedulingservice.frameworks.persistence.adapters;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.ConsultaStatus;
import br.com.medical.schedulingservice.domain.entities.ConsultaTipo;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.frameworks.persistence.mappers.ConsultaPersistenceMapperImpl;
import br.com.medical.schedulingservice.frameworks.persistence.mappers.UsuarioPersistenceMapperImpl;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integracao dos adapters de persistencia contra um MySQL real via Testcontainers,
 * validando inclusive as migrations Flyway. Requer Docker disponivel; roda na fase "verify"
 * via maven-failsafe-plugin (nao e executado pelo "mvn test" padrao).
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ConsultaRepositoryImpl.class, UsuarioRepositoryImpl.class, ConsultaPersistenceMapperImpl.class, UsuarioPersistenceMapperImpl.class})
class ConsultaRepositoryImplIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("scheduling_db_test");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private ConsultaRepositoryImpl consultaRepository;

    @Autowired
    private UsuarioRepositoryImpl usuarioRepository;

    @Test
    void deveSalvarBuscarEFiltrarConsultaPersistida() {
        Usuario paciente = usuarioRepository.salvar(Usuario.builder()
                .nome("Paciente Teste").email("paciente.it@teste.com").senha("hash").role(UserRole.PACIENTE).build());
        Usuario medico = usuarioRepository.salvar(Usuario.builder()
                .nome("Medico Teste").email("medico.it@teste.com").senha("hash").role(UserRole.MEDICO).build());

        LocalDateTime dataConsulta = LocalDateTime.now().plusDays(1).withNano(0);
        Consulta consulta = Consulta.builder()
                .pacienteId(paciente.getId())
                .profissionalId(medico.getId())
                .dataSolicitacao(LocalDateTime.now())
                .dataConsulta(dataConsulta)
                .tipo(ConsultaTipo.PRESENCIAL)
                .status(ConsultaStatus.AGENDADA)
                .build();

        Consulta salva = consultaRepository.salvar(consulta);

        assertThat(salva.getId()).isNotNull();
        assertThat(consultaRepository.buscarPorId(salva.getId())).isPresent();
        assertThat(consultaRepository.existeConflitoDeHorario(medico.getId(), dataConsulta, null)).isTrue();
        assertThat(consultaRepository.existeConflitoDeHorario(medico.getId(), dataConsulta, salva.getId())).isFalse();

        ConsultaFiltro filtro = new ConsultaFiltro(paciente.getId(), null, null, null);
        assertThat(consultaRepository.buscarComFiltros(filtro))
                .extracting(Consulta::getId)
                .contains(salva.getId());
    }
}