package br.com.medical.historyservice.presentation.history.graphql.mapper;

import br.com.medical.historyservice.domain.history.model.AppointmentHistory;
import br.com.medical.historyservice.presentation.history.graphql.response.AppointmentHistoryResponse;

public final class HistoryGraphQlMapper {

    private HistoryGraphQlMapper() {
    }

    public static AppointmentHistoryResponse toResponse(AppointmentHistory history) {
        return new AppointmentHistoryResponse(
                history.getAppointmentId(),
                history.getPatientId(),
                history.getDoctorId(),
                history.getScheduledAt().toString(),
                history.getStatus());
    }
}
