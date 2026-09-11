package br.com.medical.schedulingservice.interface_adapters.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.medical.schedulingservice.application.dtos.LoginResponse;
import br.com.medical.schedulingservice.application.services.AuthService;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Autenticacao")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Autentica via Basic Auth e emite um token JWT")
    @SecurityRequirement(name = "basicAuth")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@AuthenticationPrincipal UserDetails userDetails) {
        AuthService.LoginResultado resultado = authService.login(userDetails.getUsername());
        Usuario usuario = resultado.usuario();

        LoginResponse response = new LoginResponse(
                resultado.token().token(),
                "Bearer",
                resultado.token().expiraEmSegundos(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return ResponseEntity.ok(response);
    }
}