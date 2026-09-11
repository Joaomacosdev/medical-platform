package br.com.medical.schedulingservice.domain.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private UserRole role;
    private String telefone;
    private String especialidade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isProfissionalDeSaude() {
        return role == UserRole.MEDICO || role == UserRole.ENFERMEIRO;
    }
}