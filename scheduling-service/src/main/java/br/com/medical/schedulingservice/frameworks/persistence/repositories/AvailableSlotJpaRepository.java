package br.com.medical.schedulingservice.frameworks.persistence.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.medical.schedulingservice.frameworks.persistence.entities.AvailableSlotJpaEntity;

public interface AvailableSlotJpaRepository extends JpaRepository<AvailableSlotJpaEntity, Long> {

    List<AvailableSlotJpaEntity> findByProfissionalIdAndDisponivelTrueAndDataHoraBetweenOrderByDataHoraAsc(
            Long profissionalId, LocalDateTime inicio, LocalDateTime fim);

    Optional<AvailableSlotJpaEntity> findByProfissionalIdAndDataHora(Long profissionalId, LocalDateTime dataHora);
}