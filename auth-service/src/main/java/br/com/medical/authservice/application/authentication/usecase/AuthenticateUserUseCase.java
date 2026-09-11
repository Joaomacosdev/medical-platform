package br.com.medical.authservice.application.authentication.usecase;

import br.com.medical.authservice.application.authentication.dto.AuthenticateUserInput;
import br.com.medical.authservice.application.authentication.dto.AuthenticationOutput;
import br.com.medical.authservice.domain.authentication.gatewys.AuthenticationGateway;
import br.com.medical.authservice.domain.authentication.model.AuthenticationResult;
import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;

public class AuthenticateUserUseCase {
    private static final long TOKEN_EXPIRATION_SECONDS = 3600;

    private final AuthenticationGateway authenticationGateway;
    private final TokenGateway tokenGateway;

    public AuthenticateUserUseCase(AuthenticationGateway authenticationGateway, TokenGateway tokenGateway) {
        this.authenticationGateway = authenticationGateway;
        this.tokenGateway = tokenGateway;
    }

    public AuthenticationOutput execute(AuthenticateUserInput authenticateUserInput) {

        AuthenticationResult result = authenticationGateway.authenticate(
                authenticateUserInput.getEmail(),
                authenticateUserInput.getPassword()
        );

        String token = tokenGateway.generate(
                result.userId(),
                result.email(),
                result.role()
        );

        String refreshToken = tokenGateway.generateRefreshToken(result.userId());

        return AuthenticationOutput.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(TOKEN_EXPIRATION_SECONDS)
                .build();

    }
}
