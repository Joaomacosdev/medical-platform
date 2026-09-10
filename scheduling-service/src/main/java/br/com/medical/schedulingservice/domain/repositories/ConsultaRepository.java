package br.com.medical.schedulingservice.domain.repositories;

import java.util.List;
import java.util.Optional;

import br.com.medical.schedulingservice.domain.entities.Consulta;

public interface ConsultaRepository {

    Consulta salvar(Consulta consulta);

    Optional<Consulta> buscarPorId(Long id);

    List<Consulta> buscarComFiltros(ConsultaFiltro filtro);

    boolean existeConflitoDeHorario(Long profissionalId, java.time.LocalDateTime dataConsulta, Long consultaIdExcluida);
}