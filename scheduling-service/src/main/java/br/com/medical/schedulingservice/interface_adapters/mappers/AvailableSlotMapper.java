package br.com.medical.schedulingservice.interface_adapters.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import br.com.medical.schedulingservice.application.dtos.AvailableSlotResponse;
import br.com.medical.schedulingservice.domain.entities.AvailableSlot;

@Mapper(componentModel = "spring")
public interface AvailableSlotMapper {

    AvailableSlotResponse toResponse(AvailableSlot slot);

    List<AvailableSlotResponse> toResponseList(List<AvailableSlot> slots);
}