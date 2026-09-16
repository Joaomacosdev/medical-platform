package br.com.medical.schedulingservice.domain.usecases;

import br.com.medical.schedulingservice.domain.entities.Consulta;

public interface CriarConsultaUseCase {

    Consulta criar(NovaConsultaComando comando);
}