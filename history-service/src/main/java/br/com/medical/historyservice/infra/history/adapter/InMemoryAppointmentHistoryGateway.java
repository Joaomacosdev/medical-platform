package br.com.medical.historyservice.infra.history.adapter;

import br.com.medical.historyservice.domain.history.gateway.AppointmentHistoryGateway;
import br.com.medical.historyservice.domain.history.model.AppointmentHistory;

import java.util.List;

public class InMemoryAppointmentHistoryGateway implements AppointmentHistoryGateway {

    private final List<AppointmentHistory> histories;

    public InMemoryAppointmentHistoryGateway(List<AppointmentHistory> histories) {
        this.histories = List.copyOf(histories);
    }

    @Override
    public List<AppointmentHistory> findByPatientId(String patientId) {
        return histories.stream()
                .filter(history -> history.getPatientId().equals(patientId))
                .toList();
    }
}
