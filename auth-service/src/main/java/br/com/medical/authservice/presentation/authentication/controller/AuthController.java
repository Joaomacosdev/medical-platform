package br.com.medical.authservice.presentation.authentication.controller;

import br.com.medical.authservice.application.authentication.usecase.AuthenticateUserUseCase;
import br.com.medical.authservice.application.authentication.usecase.RefreshTokenUseCase;
import br.com.medical.authservice.presentation.authentication.controller.docs.AuthControllerDocs;
import br.com.medical.authservice.presentation.authentication.mapper.AuthenticationPresentationMapper;
import br.com.medical.authservice.presentation.authentication.requests.RefreshTokenRequest;
import br.com.medical.authservice.presentation.authentication.requests.AuthenticateUserRequest;
import br.com.medical.authservice.presentation.authentication.responses.AuthenticateUserResponse;
import br.com.medical.authservice.presentation.authentication.responses.RefreshTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/v1")
public class AuthController implements AuthControllerDocs {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, RefreshTokenUseCase refreshTokenUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<AuthenticateUserResponse> authenticateUser(@Valid @RequestBody AuthenticateUserRequest authenticateUserRequest){
        var input = AuthenticationPresentationMapper.toInput(authenticateUserRequest);
        var output = authenticateUserUseCase.execute(input);
        var response = AuthenticationPresentationMapper.toResponse(output);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest authenticateUserRefreshRequest){
       var input = AuthenticationPresentationMapper.toInput(authenticateUserRefreshRequest);
       var output = refreshTokenUseCase.execute(input);
       var response = AuthenticationPresentationMapper.toResponse(output);

       return ResponseEntity.ok(response);

    }
}
