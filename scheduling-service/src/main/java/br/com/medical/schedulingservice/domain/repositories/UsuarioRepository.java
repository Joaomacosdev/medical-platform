package br.com.medical.schedulingservice.domain.repositories;

import java.util.Optional;

import br.com.medical.schedulingservice.domain.entities.Usuario;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorAuthUserId(Long authUserId);

    Usuario salvar(Usuario usuario);
}