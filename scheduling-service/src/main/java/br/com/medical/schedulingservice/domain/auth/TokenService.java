package br.com.medical.schedulingservice.domain.auth;

import br.com.medical.schedulingservice.domain.entities.Usuario;

public interface TokenService {

    TokenEmitido gerarToken(Usuario usuario);
}