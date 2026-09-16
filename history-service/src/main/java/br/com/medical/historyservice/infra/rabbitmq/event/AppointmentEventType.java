package br.com.medical.historyservice.infra.rabbitmq.event;

public enum AppointmentEventType {
    APPOINTMENT_CREATED,
    APPOINTMENT_RESCHEDULED,
    APPOINTMENT_CANCELLED
}