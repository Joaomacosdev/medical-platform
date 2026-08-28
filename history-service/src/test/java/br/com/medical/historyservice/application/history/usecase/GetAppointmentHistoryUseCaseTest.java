package br.com.medical.historyservice.application.history.usecase;

import br.com.medical.historyservice.domain.history.gateway.AppointmentHistoryGateway;
import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAppointmentHistoryUseCaseTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-28T12:00:00Z"),
            ZoneOffset.UTC);

    @Test
    void shouldReturnCompleteHistoryOrderedByDate() {
        AppointmentHistory secondAtSameTime = history("appointment-2", "2026-08-20T10:00:00Z");
        AppointmentHistory firstAtSameTime = history("appointment-1", "2026-08-20T10:00:00Z");
        AppointmentHistory future = history("appointment-3", "2026-09-01T10:00:00Z");
        AppointmentHistoryGateway gateway = patientId -> List.of(future, secondAtSameTime, firstAtSameTime);
        GetAppointmentHistoryUseCase useCase = new GetAppointmentHistoryUseCase(gateway, FIXED_CLOCK);

        List<AppointmentHistory> result = useCase.execute("patient-1", false);

        assertThat(result)
                .extracting(AppointmentHistory::getAppointmentId)
                .containsExactly("appointment-1", "appointment-2", "appointment-3");
    }

    @Test
    void shouldReturnOnlyFutureAppointments() {
        AppointmentHistory past = history("appointment-1", "2026-08-20T10:00:00Z");
        AppointmentHistory current = history("appointment-current", "2026-08-28T12:00:00Z");
        AppointmentHistory future = history("appointment-2", "2026-09-01T10:00:00Z");
        AppointmentHistoryGateway gateway = patientId -> List.of(past, current, future);
        GetAppointmentHistoryUseCase useCase = new GetAppointmentHistoryUseCase(gateway, FIXED_CLOCK);

        List<AppointmentHistory> result = useCase.execute("patient-1", true);

        assertThat(result)
                .extracting(AppointmentHistory::getAppointmentId)
                .containsExactly("appointment-2");
    }

    @Test
    void shouldRejectBlankPatientId() {
        AppointmentHistoryGateway gateway = patientId -> List.of();
        GetAppointmentHistoryUseCase useCase = new GetAppointmentHistoryUseCase(gateway, FIXED_CLOCK);

        assertThatThrownBy(() -> useCase.execute(" ", false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("patientId nao pode ser vazio");
    }

    private AppointmentHistory history(String appointmentId, String scheduledAt) {
        return new AppointmentHistory(
                appointmentId,
                "patient-1",
                "doctor-1",
                OffsetDateTime.parse(scheduledAt),
                "SCHEDULED");
    }
}
