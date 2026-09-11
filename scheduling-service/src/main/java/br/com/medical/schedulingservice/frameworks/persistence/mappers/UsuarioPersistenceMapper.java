package br.com.medical.schedulingservice.frameworks.persistence.mappers;

import org.mapstruct.Mapper;

import br.com.medical.schedulingservice.domain.entities.Usuario;
import br.com.medical.schedulingservice.frameworks.persistence.entities.UsuarioJpaEntity;

@Mapper(componentModel = "spring")
public interface UsuarioPersistenceMapper {

    Usuario toDomain(UsuarioJpaEntity entity);

    UsuarioJpaEntity toEntity(Usuario usuario);
}