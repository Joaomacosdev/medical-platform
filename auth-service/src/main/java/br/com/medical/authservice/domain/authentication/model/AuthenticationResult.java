package br.com.medical.authservice.domain.authentication.model;

import br.com.medical.authservice.domain.user.enums.Role;

public record AuthenticationResult(
        Long userId,
        String email,
        Role role
) {
}
