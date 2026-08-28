package br.com.medical.authservice.domain.authentication.gatewys;

public interface AuthenticationGateway {
    AuthenticationResult authenticate(
            String email,
            String password
    );
}
