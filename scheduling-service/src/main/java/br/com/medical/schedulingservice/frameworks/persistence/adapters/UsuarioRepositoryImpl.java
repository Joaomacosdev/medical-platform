package br.com.medical.schedulingservice.frameworks.persistence.adapters;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.domain.repositories.UsuarioRepository;
import br.com.medical.schedulingservice.frameworks.persistence.mappers.UsuarioPersistenceMapper;
import br.com.medical.schedulingservice.frameworks.persistence.repositories.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Usuario> buscarPorAuthUserId(Long authUserId) {
        return jpaRepository.findByAuthUserId(authUserId).map(mapper::toDomain);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        var salvo = jpaRepository.save(mapper.toEntity(usuario));
        return mapper.toDomain(salvo);
    }
}