package br.com.medical.schedulingservice.frameworks.security;

import br.com.medical.schedulingservice.domain.auth.UsuarioAutenticado;
import br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada;
import br.com.medical.schedulingservice.domain.entities.*;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private static final String SECRET = "test-secret-only";
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(new JwtService(SECRET, "auth-service"), repository);

    @AfterEach void clear() { SecurityContextHolder.clearContext(); }

    private String token(String issuer, String role, String subject, Instant expiration, String secret) {
        return JWT.create().withIssuer(issuer).withSubject(subject).withClaim("email", "patient@example.com")
                .withClaim("role", role).withExpiresAt(expiration).sign(Algorithm.HMAC256(secret));
    }

    private MockHttpServletResponse call(String token, String path) throws Exception {
        var request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        request.addHeader("Authorization", "Bearer " + token);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test void mapsAuthIdToLocalIdInsteadOfUsingSubjectAsPatientId() throws Exception {
        when(repository.buscarPorAuthUserId(7L)).thenReturn(Optional.of(
                Usuario.builder().id(42L).authUserId(7L).nome("Ana").role(UserRole.PACIENTE).build()));
        var response = call(token("auth-service", "PACIENTE", "7", Instant.now().plusSeconds(60), SECRET), "/api/v1/consultas");
        assertEquals(200, response.getStatus());
        var principal = (UsuarioAutenticado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(42L, principal.id());
        verify(repository, never()).buscarPorId(anyLong());
        verify(repository, never()).buscarPorEmail(anyString());
    }

    @Test void unlinkedAccountCannotReadEvenIfLocalIdMatches() throws Exception {
        when(repository.buscarPorAuthUserId(7L)).thenReturn(Optional.empty());
        var response = call(token("auth-service", "PACIENTE", "7", Instant.now().plusSeconds(60), SECRET), "/graphql");
        assertEquals(409, response.getStatus());
        assertTrue(response.getContentAsString().contains("CADASTRO_PENDENTE"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test void onboardingUsesAuthIdentityWithoutRequiringLocalProfile() throws Exception {
        var response = call(token("auth-service", "PACIENTE", "7", Instant.now().plusSeconds(60), SECRET), "/api/v1/cadastro/me");
        assertEquals(200, response.getStatus());
        var principal = (IdentidadeAutenticada) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(7L, principal.authUserId());
        verifyNoInteractions(repository);
    }

    @Test void rejectsInvalidTokensWithoutInternalServerError() throws Exception {
        String[] tokens = {
            token("scheduling-service", "PACIENTE", "7", Instant.now().plusSeconds(60), SECRET),
            token("auth-service", "PACIENTE", "7", Instant.now().minusSeconds(60), SECRET),
            token("auth-service", "PACIENTE", "7", Instant.now().plusSeconds(60), "wrong-secret"),
            token("auth-service", "PACIENTE", "not-a-number", Instant.now().plusSeconds(60), SECRET),
            token("auth-service", "UNKNOWN", "7", Instant.now().plusSeconds(60), SECRET),
            JWT.create().withIssuer("auth-service").withSubject("7").withExpiresAt(Instant.now().plusSeconds(60))
                .sign(Algorithm.HMAC256(SECRET))
        };
        for (String token : tokens) assertEquals(401, call(token, "/api/v1/cadastro/me").getStatus());
        verifyNoInteractions(repository);
    }

    @Test void adminCannotUseClinicalOperations() throws Exception {
        assertEquals(403, call(token("auth-service", "ADMIN", "7", Instant.now().plusSeconds(60), SECRET),
                "/api/v1/consultas").getStatus());
        verifyNoInteractions(repository);
    }
}
