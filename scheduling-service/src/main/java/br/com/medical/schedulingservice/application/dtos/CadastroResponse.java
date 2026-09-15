package br.com.medical.schedulingservice.application.dtos;

import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.entities.UserRole;

public record CadastroResponse(Long id, Long authUserId, String nome, String emailContato,
                               String telefone, String especialidade, UserRole role) {
    public static CadastroResponse from(Usuario usuario) {
        return new CadastroResponse(usuario.getId(), usuario.getAuthUserId(), usuario.getNome(),
                usuario.getEmail(), usuario.getTelefone(), usuario.getEspecialidade(), usuario.getRole());
    }
}
