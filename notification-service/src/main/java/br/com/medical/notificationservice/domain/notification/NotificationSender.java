package br.com.medical.notificationservice.domain.notification;

public interface NotificationSender {
    void send(String recipient, String subject, String body);
}
