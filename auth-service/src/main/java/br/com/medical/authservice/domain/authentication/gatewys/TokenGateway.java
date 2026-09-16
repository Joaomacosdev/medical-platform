package br.com.medical.authservice.domain.authentication.gatewys;

import br.com.medical.authservice.domain.user.enums.Role;

public interface TokenGateway {
    String generate(
            Long userId,
            String email,
            Role role
    );

    String generateRefreshToken(
            Long userId
    );

    String verify(
            String token
    );

    Long getUserIdFromRefreshToken(
            String refreshToken
    );
}
