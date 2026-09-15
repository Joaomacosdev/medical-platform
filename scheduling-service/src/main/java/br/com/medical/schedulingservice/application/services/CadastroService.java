package br.com.medical.schedulingservice.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.medical.schedulingservice.application.dtos.CadastroRequest;
import br.com.medical.schedulingservice.domain.auth.IdentidadeAutenticada;
import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.exceptions.*;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CadastroService {
    private final UsuarioRepository repository;

    public Usuario buscar(Long authUserId) {
        return repository.buscarPorAuthUserId(authUserId).orElseThrow(CadastroPendenteException::new);
    }

    @Transactional
    public Usuario salvar(IdentidadeAutenticada identity, CadastroRequest request) {
        if (identity.role().equals("ADMIN")) {
            throw new AcessoNegadoException("ADMIN nao possui cadastro de atendimento.");
        }
        UserRole role = UserRole.valueOf(identity.role());
        if (role != UserRole.PACIENTE && (request.especialidade() == null || request.especialidade().isBlank())) {
            throw new ConsultaInvalidaException("Especialidade e obrigatoria para profissionais.");
        }
        Usuario usuario = repository.buscarPorAuthUserId(identity.authUserId()).orElseGet(Usuario::new);
        usuario.setAuthUserId(identity.authUserId());
        usuario.setNome(request.nome().trim());
        usuario.setEmail(request.emailContato().trim().toLowerCase(java.util.Locale.ROOT));
        usuario.setTelefone(request.telefone());
        usuario.setEspecialidade(role == UserRole.PACIENTE ? null : request.especialidade().trim());
        usuario.setRole(role);
        return repository.salvar(usuario);
    }
}
