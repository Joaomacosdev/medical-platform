package br.com.medical.schedulingservice.frameworks.persistence.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.medical.schedulingservice.frameworks.persistence.entities.ConsultaJpaEntity;

public interface ConsultaJpaRepository extends JpaRepository<ConsultaJpaEntity, Long>,
        JpaSpecificationExecutor<ConsultaJpaEntity> {

    @Query("""
            select case when count(c) > 0 then true else false end
            from ConsultaJpaEntity c
            where c.profissionalId = :profissionalId
              and c.dataConsulta = :dataConsulta
              and c.status <> 'CANCELADA'
              and (:excludeId is null or c.id <> :excludeId)
            """)
    boolean existeConflito(@Param("profissionalId") Long profissionalId,
                            @Param("dataConsulta") LocalDateTime dataConsulta,
                            @Param("excludeId") Long excludeId);
}