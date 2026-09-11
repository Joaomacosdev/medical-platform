package br.com.medical.schedulingservice.frameworks.security;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import br.com.medical.schedulingservice.domain.auth.TokenEmitido;
import br.com.medical.schedulingservice.domain.auth.TokenService;
import br.com.medical.schedulingservice.domain.auth.UsuarioAutenticado;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;

@Component
public class JwtService implements TokenService {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_NOME = "nome";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-expiration}")
    private long accessExpirationMillis;

    @Override
    public TokenEmitido gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusMillis(accessExpirationMillis);

        String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(usuario.getId()))
                .withClaim(CLAIM_EMAIL, usuario.getEmail())
                .withClaim(CLAIM_ROLE, usuario.getRole().name())
                .withClaim(CLAIM_NOME, usuario.getNome())
                .withIssuedAt(agora)
                .withExpiresAt(expiracao)
                .sign(algorithm());

        return new TokenEmitido(token, accessExpirationMillis / 1000);
    }

    public boolean tokenValido(String token) {
        try {
            verifier().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public UsuarioAutenticado extrairPrincipal(String token) {
        DecodedJWT decoded = verifier().verify(token);
        return new UsuarioAutenticado(
                Long.valueOf(decoded.getSubject()),
                decoded.getClaim(CLAIM_EMAIL).asString(),
                decoded.getClaim(CLAIM_NOME).asString(),
                UserRole.valueOf(decoded.getClaim(CLAIM_ROLE).asString())
        );
    }

    private JWTVerifier verifier() {
        return JWT.require(algorithm()).withIssuer(issuer).build();
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }
}