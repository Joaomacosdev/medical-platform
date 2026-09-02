package br.com.medical.authservice.infra.security;

import br.com.medical.authservice.domain.authentication.exception.InvalidTokenException;
import br.com.medical.authservice.domain.authentication.exception.TokenGenerationException;
import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;
import br.com.medical.authservice.domain.user.enums.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JwtTokenService implements TokenGateway {

    private final Algorithm algorithm;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;

    public JwtTokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration}") long accessExpirationMillis,
            @Value("${security.jwt.refresh-expiration}") long refreshExpirationMillis) {

        this.algorithm = Algorithm.HMAC256(secret);
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @Override
    public String generate(Long userId, String email, Role role) {
        try {

            return JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(String.valueOf(userId))
                    .withClaim("email", email)
                    .withClaim("role", role.name())
                    .withExpiresAt(expiresAt(accessExpirationMillis))
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new TokenGenerationException();
        }

    }

    @Override
    public String generateRefreshToken(Long userId) {
        try {

            return JWT.create()
                    .withIssuer("auth-service")
                    .withSubject(String.valueOf(userId))
                    .withExpiresAt(expiresAt(refreshExpirationMillis))
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new TokenGenerationException();
        }
    }

    @Override
    public String verify(String token) {
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth-service")
                    .build();

            decodedJWT = verifier.verify(token);

            return decodedJWT.getClaim("email").asString();
        } catch (JWTVerificationException exception) {
            throw new InvalidTokenException();
        }

    }

    @Override
    public Long getUserIdFromRefreshToken(String refreshToken) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth-service")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            return Long.valueOf(decodedJWT.getSubject());

        } catch (JWTVerificationException | NumberFormatException exception) {
            throw new InvalidTokenException();
        }
    }

    private Instant expiresAt(long expirationMillis) {
        return Instant.now().plusMillis(expirationMillis);
    }
}
