package br.com.medical.authservice.presentation.authentication.mapper;

import br.com.medical.authservice.application.authentication.dto.AuthenticateUserInput;
import br.com.medical.authservice.application.authentication.dto.AuthenticationOutput;
import br.com.medical.authservice.application.authentication.dto.RefreshTokenInput;
import br.com.medical.authservice.application.authentication.dto.RefreshTokenOutput;
import br.com.medical.authservice.presentation.authentication.requests.AuthenticateUserRequest;
import br.com.medical.authservice.presentation.authentication.requests.RefreshTokenRequest;
import br.com.medical.authservice.presentation.authentication.responses.AuthenticateUserResponse;
import br.com.medical.authservice.presentation.authentication.responses.RefreshTokenResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationPresentationMapper {

   public static AuthenticateUserInput toInput(AuthenticateUserRequest request){
       return AuthenticateUserInput.builder()
               .email(request.getEmail())
               .password(request.getPassword())
               .build();
   }

   public static AuthenticateUserResponse toResponse(AuthenticationOutput  output){
       return new AuthenticateUserResponse(
               output.getAccessToken(),
               output.getRefreshToken(),
               output.getTokenType(),
               output.getExpiresIn()
       );
   }

    public static RefreshTokenInput toInput(RefreshTokenRequest request) {
        return  RefreshTokenInput.builder()
                .refreshToken(request.getRefreshToken())
                .build();
    }

    public static RefreshTokenResponse toResponse(RefreshTokenOutput output) {
        return new RefreshTokenResponse(
                output.getAccessToken(),
                output.getRefreshToken()
        );
    }
}
