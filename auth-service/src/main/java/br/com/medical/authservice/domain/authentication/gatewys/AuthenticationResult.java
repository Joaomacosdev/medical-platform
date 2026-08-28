package br.com.medical.authservice.domain.authentication.gatewys;

import br.com.medical.authservice.domain.user.enums.Role;

public record AuthenticationResult(
        Long userId,
        String email,
        Role role
) {
}
