package br.com.medical.notificationservice.application.notification;

import br.com.medical.notificationservice.domain.notification.NotificationSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
public class SendEmailNotificationService {
    private final NotificationSender notificationSender;

    public SendEmailNotificationService(@Qualifier("emailSender") NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    public void send(String recipient, String subject, String body) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient is required");
        }
        notificationSender.send(recipient, subject, body);
    }

    public void sendConsultationNotification(String recipient, String doctorName,
                                            String medicalSpecialty, LocalDateTime consultationDate) {
        Objects.requireNonNull(consultationDate, "Consultation date is required");
        if (doctorName == null || doctorName.isBlank()
                || medicalSpecialty == null || medicalSpecialty.isBlank()) {
            throw new IllegalArgumentException("Doctor name and specialty are required");
        }
        String body = """
                Olá!

                Confira os dados da sua consulta:
                Médico(a): %s
                Especialidade: %s
                Data e horário: %s

                Equipe Medical
                """.formatted(doctorName, medicalSpecialty,
                consultationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        send(recipient, "Notificação de consulta", body);
    }
}
