package br.com.medical.schedulingservice.domain.usecases;

import java.util.List;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.UserRole;

public interface ListarConsultasUseCase {

    List<Consulta> listar(ListarConsultasComando comando);

    Consulta buscarPorId(Long consultaId, Long solicitanteId, UserRole solicitanteRole);
}