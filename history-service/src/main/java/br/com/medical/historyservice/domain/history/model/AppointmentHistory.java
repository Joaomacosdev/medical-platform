package br.com.medical.historyservice.domain.history.model;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AppointmentHistory {

    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final OffsetDateTime scheduledAt;
    private final String status;

    public AppointmentHistory(
            String appointmentId,
            String patientId,
            String doctorId,
            OffsetDateTime scheduledAt,
            String status) {
        this.appointmentId = requireText(appointmentId, "appointmentId");
        this.patientId = requireText(patientId, "patientId");
        this.doctorId = doctorId;
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt nao pode ser nulo");
        this.status = requireText(status, "status");
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nao pode ser vazio");
        }
        return value;
    }
}
