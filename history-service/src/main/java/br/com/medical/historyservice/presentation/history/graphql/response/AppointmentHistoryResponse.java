package br.com.medical.historyservice.presentation.history.graphql.response;

public class AppointmentHistoryResponse {

    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final String scheduledAt;
    private final String status;

    public AppointmentHistoryResponse(
            String appointmentId,
            String patientId,
            String doctorId,
            String scheduledAt,
            String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduledAt = scheduledAt;
        this.status = status;
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

    public String getScheduledAt() {
        return scheduledAt;
    }

    public String getStatus() {
        return status;
    }
}
