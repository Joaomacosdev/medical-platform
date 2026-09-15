package br.com.medical.historyservice.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "appointment_events")
public class AppointmentEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, length = 36)
    private String eventId;

    @Column(name = "appointment_id", nullable = false)
    private String appointmentId;

    @Column(name = "patient_id", nullable = false)
    private String patientId;

    @Column(name = "doctor_id")
    private String doctorId;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "status", nullable = false)
    private String status;

    protected AppointmentEventEntity() {
    }

    public AppointmentEventEntity(
            String eventId,
            String appointmentId,
            String patientId,
            String doctorId,
            OffsetDateTime scheduledAt,
            String status) {
        this.eventId = eventId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.scheduledAt = scheduledAt;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
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
}