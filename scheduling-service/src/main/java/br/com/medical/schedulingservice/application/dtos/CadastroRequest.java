package br.com.medical.schedulingservice.application.dtos;

import jakarta.validation.constraints.*;

public record CadastroRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 150) String emailContato,
    @Size(max = 20) String telefone,
    @Size(max = 100) String especialidade
) {}
