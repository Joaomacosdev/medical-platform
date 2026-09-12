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

    public void sendConsultationNotification(String recipient, String patientName, String doctorName,
                                            String medicalSpecialty, LocalDateTime consultationDate) {
        Objects.requireNonNull(consultationDate, "Consultation date is required");
        if (patientName == null || patientName.isBlank()
                || doctorName == null || doctorName.isBlank()
                || medicalSpecialty == null || medicalSpecialty.isBlank()) {
            throw new IllegalArgumentException("Patient name, doctor name and specialty are required");
        }
        String body = """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333333; margin: 0; padding: 0;">
                    <div style="max-width: 480px; margin: 0 auto; padding: 24px; border: 1px solid #e0e0e0; border-radius: 8px;">
                        <h2 style="color: #2c6ecb; margin-top: 0;">Notificação de Consulta</h2>
                        <p>Olá, %s!</p>
                        <p>Confira os dados da sua consulta:</p>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 12px;">
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold;">Médico(a):</td>
                                <td style="padding: 8px 0;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold;">Especialidade:</td>
                                <td style="padding: 8px 0;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 8px 0; font-weight: bold;">Data e horário:</td>
                                <td style="padding: 8px 0;">%s</td>
                            </tr>
                        </table>
                        <p style="margin-top: 24px; color: #777777;">Equipe Medical</p>
                    </div>
                </body>
                </html>
                """.formatted(patientName, doctorName, medicalSpecialty,
                consultationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        send(recipient, "Notificação de consulta", body);
    }
}