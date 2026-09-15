package br.com.medical.historyservice.infra.persistence.adapter;

import br.com.medical.historyservice.domain.history.gateway.AppointmentHistoryGateway;
import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import br.com.medical.historyservice.infra.persistence.entity.AppointmentEventEntity;
import br.com.medical.historyservice.infra.persistence.repository.AppointmentEventJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaAppointmentHistoryGateway implements AppointmentHistoryGateway {

    private final AppointmentEventJpaRepository repository;

    public JpaAppointmentHistoryGateway(AppointmentEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AppointmentHistory> findByPatientId(String patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AppointmentHistory toDomain(AppointmentEventEntity entity) {
        return new AppointmentHistory(
                entity.getAppointmentId(),
                entity.getPatientId(),
                entity.getDoctorId(),
                entity.getScheduledAt(),
                entity.getStatus());
    }
}