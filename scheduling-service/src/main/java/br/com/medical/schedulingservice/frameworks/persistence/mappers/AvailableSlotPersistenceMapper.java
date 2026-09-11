package br.com.medical.schedulingservice.frameworks.persistence.mappers;

import org.mapstruct.Mapper;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;
import br.com.medical.schedulingservice.frameworks.persistence.entities.AvailableSlotJpaEntity;

@Mapper(componentModel = "spring")
public interface AvailableSlotPersistenceMapper {

    AvailableSlot toDomain(AvailableSlotJpaEntity entity);

    AvailableSlotJpaEntity toEntity(AvailableSlot slot);
}