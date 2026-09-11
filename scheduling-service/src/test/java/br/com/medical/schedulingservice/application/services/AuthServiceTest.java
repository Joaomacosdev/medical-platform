package br.com.medical.schedulingservice.application.services;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.medical.schedulingservice.domain.auth.TokenEmitido;
import br.com.medical.schedulingservice.domain.auth.TokenService;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.exceptions.UsuarioNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveEmitirTokenParaUsuarioAutenticado() {
        Usuario usuario = Usuario.builder().id(1L).email("medico@teste.com").nome("Dr. House").role(UserRole.MEDICO).build();
        TokenEmitido token = new TokenEmitido("jwt-token", 3600L);

        when(usuarioRepository.buscarPorEmail("medico@teste.com")).thenReturn(Optional.of(usuario));
        when(tokenService.gerarToken(usuario)).thenReturn(token);

        AuthService.LoginResultado resultado = authService.login("medico@teste.com");

        assertThat(resultado.token().token()).isEqualTo("jwt-token");
        assertThat(resultado.usuario()).isEqualTo(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.buscarPorEmail("inexistente@teste.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("inexistente@teste.com"))
                .isInstanceOf(UsuarioNotFoundException.class);
    }
}