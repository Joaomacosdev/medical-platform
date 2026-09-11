package br.com.medical.schedulingservice.application.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasComando;
import br.com.medical.schedulingservice.domain.usecases.ListarConsultasUseCase;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListarConsultasService implements ListarConsultasUseCase {

    private final ConsultaRepository consultaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Consulta> listar(ListarConsultasComando comando) {
        ConsultaFiltro filtro = comando.filtro();
        if (comando.solicitanteRole() == UserRole.PACIENTE) {
            filtro = new ConsultaFiltro(comando.solicitanteId(), filtro.profissionalId(), filtro.status(), filtro.data());
        }
        return consultaRepository.buscarComFiltros(filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public Consulta buscarPorId(Long consultaId, Long solicitanteId, UserRole solicitanteRole) {
        Consulta consulta = consultaRepository.buscarPorId(consultaId)
                .orElseThrow(() -> new ConsultaNotFoundException(consultaId));

        if (solicitanteRole == UserRole.PACIENTE && !consulta.pertenceAoPaciente(solicitanteId)) {
            throw new AcessoNegadoException("Voce nao tem permissao para visualizar esta consulta.");
        }

        return consulta;
    }
}