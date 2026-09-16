package br.com.medical.schedulingservice.interface_adapters.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import br.com.medical.schedulingservice.application.dtos.ConsultaResponse;
import br.com.medical.schedulingservice.domain.entities.Consulta;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    ConsultaResponse toResponse(Consulta consulta);

    List<ConsultaResponse> toResponseList(List<Consulta> consultas);
}