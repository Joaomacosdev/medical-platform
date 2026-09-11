package br.com.medical.schedulingservice.frameworks.persistence.adapters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.medical.schedulingservice.domain.entities.AvailableSlot;
import br.com.medical.schedulingservice.domain.repositories.AvailableSlotRepository;
import br.com.medical.schedulingservice.frameworks.persistence.mappers.AvailableSlotPersistenceMapper;
import br.com.medical.schedulingservice.frameworks.persistence.repositories.AvailableSlotJpaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AvailableSlotRepositoryImpl implements AvailableSlotRepository {

    private final AvailableSlotJpaRepository jpaRepository;
    private final AvailableSlotPersistenceMapper mapper;

    @Override
    public List<AvailableSlot> buscarDisponiveisPorProfissionalEData(Long profissionalId, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return jpaRepository
                .findByProfissionalIdAndDisponivelTrueAndDataHoraBetweenOrderByDataHoraAsc(profissionalId, inicio, fim)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<AvailableSlot> buscarPorProfissionalEDataHora(Long profissionalId, LocalDateTime dataHora) {
        return jpaRepository.findByProfissionalIdAndDataHora(profissionalId, dataHora).map(mapper::toDomain);
    }

    @Override
    public AvailableSlot salvar(AvailableSlot slot) {
        var salvo = jpaRepository.save(mapper.toEntity(slot));
        return mapper.toDomain(salvo);
    }
}