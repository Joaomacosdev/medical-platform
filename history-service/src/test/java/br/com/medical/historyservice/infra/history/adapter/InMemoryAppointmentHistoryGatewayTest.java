package br.com.medical.historyservice.infra.history.adapter;

import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAppointmentHistoryGatewayTest {

    @Test
    void shouldReturnOnlyHistoriesFromRequestedPatient() {
        AppointmentHistory firstPatientHistory = history("appointment-1", "patient-1");
        AppointmentHistory secondPatientHistory = history("appointment-2", "patient-2");
        InMemoryAppointmentHistoryGateway gateway = new InMemoryAppointmentHistoryGateway(
                List.of(firstPatientHistory, secondPatientHistory));

        List<AppointmentHistory> result = gateway.findByPatientId("patient-1");

        assertThat(result)
                .extracting(AppointmentHistory::getAppointmentId)
                .containsExactly("appointment-1");
    }

    private AppointmentHistory history(String appointmentId, String patientId) {
        return new AppointmentHistory(
                appointmentId,
                patientId,
                "doctor-1",
                OffsetDateTime.parse("2026-09-01T10:00:00Z"),
                "SCHEDULED");
    }
}
