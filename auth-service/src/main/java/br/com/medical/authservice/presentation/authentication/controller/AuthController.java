package br.com.medical.authservice.presentation.authentication.controller;

import br.com.medical.authservice.application.authentication.dto.AuthenticateUserInput;
import br.com.medical.authservice.application.authentication.dto.AuthenticationOutput;
import br.com.medical.authservice.application.authentication.usecase.AuthenticateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase
    ) {
        this.authenticateUserUseCase =
                authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationOutput> login(
            @RequestBody AuthenticateUserInput input
    ) {

        AuthenticationOutput output =
                authenticateUserUseCase.execute(input);

        return ResponseEntity.ok(output);
    }

}
