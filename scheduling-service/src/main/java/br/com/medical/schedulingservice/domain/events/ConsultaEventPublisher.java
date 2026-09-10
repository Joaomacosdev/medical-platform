package br.com.medical.schedulingservice.domain.events;

import br.com.medical.schedulingservice.domain.entities.Consulta;

public interface ConsultaEventPublisher {

    void publicarConsultaCriada(Consulta consulta);

    void publicarConsultaEditada(Consulta consulta);
}