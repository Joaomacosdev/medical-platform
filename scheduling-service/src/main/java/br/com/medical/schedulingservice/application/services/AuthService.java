package br.com.medical.schedulingservice.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.auth.TokenEmitido;
import br.com.medical.schedulingservice.domain.auth.TokenService;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public LoginResultado login(String email) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));

        TokenEmitido tokenEmitido = tokenService.gerarToken(usuario);
        log.info("Token emitido para usuario {} ({})", usuario.getEmail(), usuario.getRole());

        return new LoginResultado(tokenEmitido, usuario);
    }

    public record LoginResultado(TokenEmitido token, Usuario usuario) {
    }
}