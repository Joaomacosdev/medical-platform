package br.com.medical.schedulingservice.frameworks.persistence.adapters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.medical.schedulingservice.domain.entities.Consulta;
import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.domain.repositories.ConsultaRepository;
import br.com.medical.schedulingservice.frameworks.persistence.mappers.ConsultaPersistenceMapper;
import br.com.medical.schedulingservice.frameworks.persistence.repositories.ConsultaJpaRepository;
import br.com.medical.schedulingservice.frameworks.persistence.specification.ConsultaSpecifications;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ConsultaRepositoryImpl implements ConsultaRepository {

    private final ConsultaJpaRepository jpaRepository;
    private final ConsultaPersistenceMapper mapper;

    @Override
    public Consulta salvar(Consulta consulta) {
        var salva = jpaRepository.save(mapper.toEntity(consulta));
        return mapper.toDomain(salva);
    }

    @Override
    public Optional<Consulta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Consulta> buscarComFiltros(ConsultaFiltro filtro) {
        return jpaRepository.findAll(ConsultaSpecifications.fromFiltro(filtro))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflitoDeHorario(Long profissionalId, LocalDateTime dataConsulta, Long consultaIdExcluida) {
        return jpaRepository.existeConflito(profissionalId, dataConsulta, consultaIdExcluida);
    }
}