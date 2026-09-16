package br.com.medical.schedulingservice.domain.auth;

import br.com.medical.schedulingservice.domain.entities.UserRole;

public record UsuarioAutenticado(Long id, String email, String nome, UserRole role) {
}