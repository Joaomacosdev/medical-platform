package br.com.medical.historyservice.infra.persistence.repository;

import br.com.medical.historyservice.infra.persistence.entity.AppointmentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentEventJpaRepository extends JpaRepository<AppointmentEventEntity, String> {

    List<AppointmentEventEntity> findByPatientId(String patientId);
}