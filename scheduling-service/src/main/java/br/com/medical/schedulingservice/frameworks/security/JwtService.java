package br.com.medical.schedulingservice.frameworks.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada;

@Component
public class JwtService {
    private final Algorithm algorithm;
    private final String issuer;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.issuer}") String issuer) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public IdentidadeAutenticada verificar(String token) {
        var jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        Long id = Long.valueOf(jwt.getSubject());
        String email = jwt.getClaim("email").asString();
        String role = jwt.getClaim("role").asString();
        if (id <= 0 || jwt.getExpiresAt() == null || email == null || email.isBlank()
                || role == null || !java.util.Set.of("PACIENTE", "MEDICO", "ENFERMEIRO", "ADMIN").contains(role)) {
            throw new IllegalArgumentException("Token de acesso invalido");
        }
        return new IdentidadeAutenticada(id, email, role);
    }
}
