package br.com.medical.notificationservice.infra.messaging;

import br.com.medical.notificationservice.application.notification.SendEmailNotificationService;
import br.com.medical.notificationservice.infra.dto.ConsultationDTO;
import br.com.medical.notificationservice.infra.dto.NotificationMessageDTO;
import br.com.medical.notificationservice.infra.email.SmtpEmailSender;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class EmailConsumerTest {
    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final Channel channel = mock(Channel.class);
    private final EmailConsumer consumer = new EmailConsumer(new SendEmailNotificationService(
            new SmtpEmailSender(mailSender, "sender@example.test")));

    private NotificationMessageDTO notification() {
        return new NotificationMessageDTO(1L, "patient@example.test", null,
                new ConsultationDTO(2L, "Dra. Ana", "Cardiologia",
                        LocalDateTime.of(2026, 10, 20, 14, 30)));
    }

    private Message message() {
        MessageProperties properties = new MessageProperties();
        properties.setDeliveryTag(7L);
        return new Message(new byte[0], properties);
    }

    @Test
    void sendsConsultationEmailBeforeAcknowledging() throws IOException {
        consumer.notificationConsumer(notification(), channel, message());

        var email = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        var order = inOrder(mailSender, channel);
        order.verify(mailSender).send(email.capture());
        order.verify(channel).basicAck(7L, false);
        verifyNoMoreInteractions(channel);
        assertEquals("sender@example.test", email.getValue().getFrom());
        assertArrayEquals(new String[]{"patient@example.test"}, email.getValue().getTo());
        assertEquals("Notificação de consulta", email.getValue().getSubject());
        assertTrue(email.getValue().getText().contains("Dra. Ana"));
        assertTrue(email.getValue().getText().contains("Cardiologia"));
        assertTrue(email.getValue().getText().contains("20/10/2026 14:30"));
    }

    @Test
    void rejectsWithoutRequeueWhenSmtpFails() throws IOException {
        doThrow(new MailSendException("Simulated SMTP failure"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        consumer.notificationConsumer(notification(), channel, message());

        verify(channel).basicNack(7L, false, false);
        verifyNoMoreInteractions(channel);
    }

    @Test
    void doesNotRejectAfterAcknowledgementFails() throws IOException {
        doThrow(new IOException("Simulated channel failure"))
                .when(channel).basicAck(7L, false);

        assertThrows(IOException.class,
                () -> consumer.notificationConsumer(notification(), channel, message()));

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(channel).basicAck(7L, false);
        verifyNoMoreInteractions(channel);
    }

    @Test
    void rejectsInvalidRecipientWithoutSending() throws IOException {
        var invalid = new NotificationMessageDTO(1L, " ", null, notification().consultation());

        consumer.notificationConsumer(invalid, channel, message());

        verifyNoInteractions(mailSender);
        verify(channel).basicNack(7L, false, false);
        verifyNoMoreInteractions(channel);
    }
}
