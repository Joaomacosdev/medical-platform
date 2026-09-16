package br.com.medical.historyservice.application.history.usecase;

import br.com.medical.historyservice.domain.history.gateway.AppointmentHistoryGateway;
import br.com.medical.historyservice.domain.history.model.AppointmentHistory;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GetAppointmentHistoryUseCase {

    private final AppointmentHistoryGateway appointmentHistoryGateway;
    private final Clock clock;

    public GetAppointmentHistoryUseCase(
            AppointmentHistoryGateway appointmentHistoryGateway,
            Clock clock) {
        this.appointmentHistoryGateway = Objects.requireNonNull(appointmentHistoryGateway);
        this.clock = Objects.requireNonNull(clock);
    }

    public List<AppointmentHistory> execute(String patientId, boolean futureOnly) {
        validatePatientId(patientId);

        Stream<AppointmentHistory> histories = appointmentHistoryGateway
                .findByPatientId(patientId)
                .stream();

        if (futureOnly) {
            OffsetDateTime now = OffsetDateTime.now(clock);
            histories = histories.filter(history -> history.getScheduledAt().isAfter(now));
        }

        return histories
                .sorted(Comparator
                        .comparing(AppointmentHistory::getScheduledAt)
                        .thenComparing(AppointmentHistory::getAppointmentId))
                .toList();
    }

    private void validatePatientId(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("patientId nao pode ser vazio");
        }
    }
}
