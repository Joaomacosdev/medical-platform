CREATE TABLE appointment_events (
    event_id VARCHAR(36) PRIMARY KEY,
    appointment_id VARCHAR(255) NOT NULL,
    patient_id VARCHAR(255) NOT NULL,
    doctor_id VARCHAR(255),
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(100) NOT NULL
);

CREATE INDEX idx_appointment_events_patient_id
    ON appointment_events (patient_id);