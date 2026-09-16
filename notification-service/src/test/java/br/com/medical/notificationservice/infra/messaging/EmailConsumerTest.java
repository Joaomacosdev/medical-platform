package br.com.medical.notificationservice.infra.messaging;

import br.com.medical.notificationservice.application.notification.SendEmailNotificationService;
import br.com.medical.notificationservice.infra.dto.ConsultationDTO;
import br.com.medical.notificationservice.infra.dto.NotificationMessageDTO;
import br.com.medical.notificationservice.infra.email.SmtpEmailSender;
import com.rabbitmq.client.Channel;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class EmailConsumerTest {
    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final Channel channel = mock(Channel.class);
    private final EmailConsumer consumer = new EmailConsumer(new SendEmailNotificationService(
            new SmtpEmailSender(mailSender, "sender@example.test")));

    private NotificationMessageDTO notification() {
        return new NotificationMessageDTO(1L, "Joao Lima", "patient@example.test", null,
                new ConsultationDTO(2L, "Dra. Ana", "Cardiologia",
                        LocalDateTime.of(2026, 10, 20, 14, 30)));
    }

    private Message message() {
        MessageProperties properties = new MessageProperties();
        properties.setDeliveryTag(7L);
        return new Message(new byte[0], properties);
    }

    @Test
    void sendsConsultationEmailBeforeAcknowledging() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        consumer.notificationConsumer(notification(), channel, message());

        var order = inOrder(mailSender, channel);
        order.verify(mailSender).send(any(MimeMessage.class));
        order.verify(channel).basicAck(7L, false);
        verifyNoMoreInteractions(channel);
    }

    @Test
    void rejectsWithoutRequeueWhenSmtpFails() throws IOException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Simulated SMTP failure"))
                .when(mailSender).send(any(MimeMessage.class));

        consumer.notificationConsumer(notification(), channel, message());

        verify(channel).basicNack(7L, false, false);
        verifyNoMoreInteractions(channel);
    }

    @Test
    void rejectsInvalidRecipientWithoutSending() throws IOException {
        var invalid = new NotificationMessageDTO(1L, "Joao Lima", " ", null, notification().consultation());

        consumer.notificationConsumer(invalid, channel, message());

        verifyNoInteractions(mailSender);
        verify(channel).basicNack(7L, false, false);
        verifyNoMoreInteractions(channel);
    }
}