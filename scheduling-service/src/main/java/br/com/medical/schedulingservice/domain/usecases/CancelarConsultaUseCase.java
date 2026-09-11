package br.com.medical.schedulingservice.domain.usecases;

import br.com.medical.schedulingservice.domain.entities.Consulta;

public interface CancelarConsultaUseCase {

    Consulta cancelar(CancelarConsultaComando comando);
}