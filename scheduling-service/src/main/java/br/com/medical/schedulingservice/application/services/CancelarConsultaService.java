package br.com.medical.schedulingservice.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.entities.UserRole;
import br.com.medical.schedulingservice.domain.exceptions.AcessoNegadoException;
import br.com.medical.schedulingservice.domain.exceptions.ConsultaNotFoundException;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaComando;
import br.com.medical.schedulingservice.domain.usecases.CancelarConsultaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelarConsultaService implements CancelarConsultaUseCase {

    private final ConsultaRepository consultaRepository;

    @Override
    @Transactional
    public Consulta cancelar(CancelarConsultaComando comando) {
        if (comando.solicitanteRole() != UserRole.MEDICO && comando.solicitanteRole() != UserRole.ENFERMEIRO) {
            throw new AcessoNegadoException("Apenas medicos e enfermeiros podem cancelar consultas.");
        }

        Consulta consulta = consultaRepository.buscarPorId(comando.consultaId())
                .orElseThrow(() -> new ConsultaNotFoundException(comando.consultaId()));

        consulta.cancelar();
        Consulta cancelada = consultaRepository.salvar(consulta);
        log.info("Consulta {} cancelada por usuario {}", cancelada.getId(), comando.solicitanteId());

        return cancelada;
    }
}