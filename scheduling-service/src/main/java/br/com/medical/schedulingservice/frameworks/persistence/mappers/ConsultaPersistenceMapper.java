package br.com.medical.schedulingservice.frameworks.persistence.mappers;

import org.mapstruct.Mapper;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.frameworks.persistence.entities.ConsultaJpaEntity;

@Mapper(componentModel = "spring")
public interface ConsultaPersistenceMapper {

    Consulta toDomain(ConsultaJpaEntity entity);

    ConsultaJpaEntity toEntity(Consulta consulta);
}