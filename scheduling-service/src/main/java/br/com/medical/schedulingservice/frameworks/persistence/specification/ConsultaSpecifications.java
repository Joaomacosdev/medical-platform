package br.com.medical.schedulingservice.frameworks.persistence.specification;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import br.com.medical.schedulingservice.domain.repositories.ConsultaFiltro;
import br.com.medical.schedulingservice.frameworks.persistence.entities.ConsultaJpaEntity;

public final class ConsultaSpecifications {

    private ConsultaSpecifications() {
    }

    public static Specification<ConsultaJpaEntity> fromFiltro(ConsultaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.pacienteId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("pacienteId"), filtro.pacienteId()));
            }
            if (filtro.profissionalId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("profissionalId"), filtro.profissionalId()));
            }
            if (filtro.status() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), filtro.status()));
            }
            if (filtro.data() != null) {
                LocalDateTime inicioDia = filtro.data().atStartOfDay();
                LocalDateTime fimDia = filtro.data().plusDays(1).atStartOfDay();
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("dataConsulta"), inicioDia));
                predicates = cb.and(predicates, cb.lessThan(root.get("dataConsulta"), fimDia));
            }

            return predicates;
        };
    }
}