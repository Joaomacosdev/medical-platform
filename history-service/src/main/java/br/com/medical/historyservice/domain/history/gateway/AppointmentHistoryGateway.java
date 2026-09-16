package br.com.medical.historyservice.domain.history.gateway;

import br.com.medical.historyservice.domain.history.model.AppointmentHistory;

import java.util.List;

public interface AppointmentHistoryGateway {

    List<AppointmentHistory> findByPatientId(String patientId);
}
