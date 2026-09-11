package br.com.medical.authservice.application.authentication.usecase;

import br.com.medical.authservice.application.authentication.dto.RefreshTokenInput;
import br.com.medical.authservice.application.authentication.dto.RefreshTokenOutput;
import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;

public class RefreshTokenUseCase {

    private final UserGateway userGateway;
    private final TokenGateway tokenGateway;

    public RefreshTokenUseCase(UserGateway userGateway, TokenGateway tokenGateway) {
        this.userGateway = userGateway;
        this.tokenGateway = tokenGateway;
    }


    public RefreshTokenOutput execute(RefreshTokenInput input){

        Long userId = tokenGateway.getUserIdFromRefreshToken(
                input.getRefreshToken()
        );

        User user = userGateway.findById(userId)
                .orElseThrow(() -> new  UserNotFoundException(userId));

        String accessToken = tokenGateway.generate(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        String refreshToken = tokenGateway.generateRefreshToken(
                user.getId()
        );

        return RefreshTokenOutput.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
