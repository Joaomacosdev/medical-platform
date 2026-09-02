package br.com.medical.authservice.domain.authentication.gatewys;

import br.com.medical.authservice.domain.authentication.model.AuthenticationResult;

public interface AuthenticationGateway {
    AuthenticationResult authenticate(
            String email,
            String password
    );
}
