package br.com.medical.authservice.infra.config;

import br.com.medical.authservice.application.authentication.dto.RefreshTokenInput;
import br.com.medical.authservice.application.authentication.usecase.AuthenticateUserUseCase;
import br.com.medical.authservice.application.authentication.usecase.RefreshTokenUseCase;
import br.com.medical.authservice.domain.authentication.gatewys.AuthenticationGateway;
import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            AuthenticationGateway authenticationGateway,
            TokenGateway tokenGateway){
        return new AuthenticateUserUseCase(authenticationGateway, tokenGateway);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(UserGateway userGateway, TokenGateway tokenGateway){
        return new RefreshTokenUseCase(userGateway, tokenGateway);

    }

}
