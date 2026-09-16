package br.com.medical.schedulingservice.frameworks.security;

import br.com.medical.schedulingservice.application.services.CadastroService;
import br.com.medical.schedulingservice.domain.entities.*;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.interface_adapters.controllers.CadastroController;
import br.com.medical.schedulingservice.interface_adapters.controllers.ConsultaController;
import br.com.medical.schedulingservice.interface_adapters.exceptionhandling.GlobalExceptionHandler;
import br.com.medical.schedulingservice.domain.usecases.*;
import br.com.medical.schedulingservice.interface_adapters.mappers.ConsultaMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CadastroHttpTest {
    @org.springframework.boot.test.context.TestConfiguration @EnableWebMvc @EnableWebSecurity
    @Import(SecurityConfig.class)
    static class Config {
        @Bean UsuarioRepository repository() { return mock(UsuarioRepository.class); }
        @Bean JwtService jwtService() { return new JwtService("test-secret", "auth-service"); }
        @Bean JwtAuthenticationFilter jwtFilter(JwtService jwt, UsuarioRepository repo) { return new JwtAuthenticationFilter(jwt, repo); }
        @Bean CadastroService cadastroService(UsuarioRepository repo) { return new CadastroService(repo); }
        @Bean CadastroController cadastroController(CadastroService service) { return new CadastroController(service); }
        @Bean GlobalExceptionHandler exceptionHandler() { return new GlobalExceptionHandler(); }
        @Bean ConsultaController consultaController() {
            return new ConsultaController(mock(CriarConsultaUseCase.class), mock(EditarConsultaUseCase.class),
                mock(ListarConsultasUseCase.class), mock(CancelarConsultaUseCase.class), mock(ConsultaMapper.class));
        }
    }

    AnnotationConfigWebApplicationContext context;
    MockMvc mvc;
    String bearer;

    @BeforeEach void setup() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(Config.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        bearer = "Bearer " + JWT.create().withIssuer("auth-service").withSubject("7007")
            .withClaim("email", "login@example.com").withClaim("role", "PACIENTE")
            .withExpiresAt(Instant.now().plusSeconds(300)).sign(Algorithm.HMAC256("test-secret"));
        var stored = new AtomicReference<Usuario>();
        var repo = context.getBean(UsuarioRepository.class);
        when(repo.buscarPorAuthUserId(7007L)).thenAnswer(call -> Optional.ofNullable(stored.get()));
        when(repo.salvar(any())).thenAnswer(call -> {
            Usuario user = call.getArgument(0); user.setId(42L); stored.set(user); return user;
        });
    }

    @AfterEach void close() { context.close(); }

    @Test void completeProfileAndPreserveIdentityThroughRealSecurityChain() throws Exception {
        mvc.perform(get("/api/v1/consultas").servletPath("/api/v1/consultas"))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/consultas").servletPath("/api/v1/consultas").header("Authorization", bearer))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("CADASTRO_PENDENTE"));
        String body = "{\"nome\":\"Ana\",\"emailContato\":\"contact@example.com\",\"authUserId\":3}";
        for (int i=0; i<2; i++) {
            mvc.perform(put("/api/v1/cadastro/me").servletPath("/api/v1/cadastro/me")
                .header("Authorization", bearer).contentType("application/json").content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.authUserId").value(7007));
        }
        mvc.perform(get("/api/v1/cadastro/me").servletPath("/api/v1/cadastro/me").header("Authorization", bearer))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(42));
        mvc.perform(post("/api/v1/consultas").servletPath("/api/v1/consultas")
            .header("Authorization", bearer).contentType("application/json")
            .content("{\"pacienteId\":42,\"profissionalId\":1,\"dataConsulta\":\"2099-01-01T10:00:00\",\"tipo\":\"PRESENCIAL\"}"))
            .andExpect(status().isForbidden());
    }

    @Test void validatesProfileFieldsAndRejectsAdmin() throws Exception {
        mvc.perform(put("/api/v1/cadastro/me").servletPath("/api/v1/cadastro/me")
            .header("Authorization", bearer).contentType("application/json").content("{}"))
            .andExpect(status().isBadRequest());
        String admin = "Bearer " + JWT.create().withIssuer("auth-service").withSubject("1")
            .withClaim("email", "admin@example.com").withClaim("role", "ADMIN")
            .withExpiresAt(Instant.now().plusSeconds(60)).sign(Algorithm.HMAC256("test-secret"));
        mvc.perform(get("/api/v1/cadastro/me").servletPath("/api/v1/cadastro/me").header("Authorization", admin))
            .andExpect(status().isForbidden());
    }
}
