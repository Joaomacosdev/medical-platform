package br.com.medical.authservice.infra.security;

import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;
import br.com.medical.authservice.domain.user.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService implements TokenGateway {
    private final Algorithm algorithm;
    private final long expirationMillis;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long expirationMillis
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String generate(
            Long userId,
            String email,
            Role role
    ) {

        Date issuedAt = new Date();

        Date expiresAt = new Date(
                issuedAt.getTime() + expirationMillis
        );

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withClaim("role", role.name())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
