package br.com.medical.historyservice.infra.persistence.adapter;

import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import br.com.medical.historyservice.infra.persistence.entity.AppointmentEventEntity;
import br.com.medical.historyservice.infra.persistence.repository.AppointmentEventJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaAppointmentHistoryGatewayTest {

    @Autowired
    private JpaAppointmentHistoryGateway gateway;

    @Autowired
    private AppointmentEventJpaRepository repository;

    @Test
    void shouldReturnPersistedHistoryByPatientId() {
        repository.saveAll(List.of(
                new AppointmentEventEntity(
                        "event-1",
                        "appointment-1",
                        "patient-1",
                        "doctor-1",
                        OffsetDateTime.parse("2026-09-01T10:00:00Z"),
                        "SCHEDULED"),
                new AppointmentEventEntity(
                        "event-2",
                        "appointment-2",
                        "patient-2",
                        "doctor-2",
                        OffsetDateTime.parse("2026-09-02T10:00:00Z"),
                        "CANCELLED")));

        List<AppointmentHistory> result = gateway.findByPatientId("patient-1");

        assertThat(result)
                .singleElement()
                .extracting(
                        AppointmentHistory::getAppointmentId,
                        AppointmentHistory::getPatientId,
                        AppointmentHistory::getDoctorId,
                        AppointmentHistory::getStatus)
                .containsExactly("appointment-1", "patient-1", "doctor-1", "SCHEDULED");
    }
}