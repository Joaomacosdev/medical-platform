package br.com.medical.notificationservice.infra.messaging;

import br.com.medical.notificationservice.application.notification.SendEmailNotificationService;
import br.com.medical.notificationservice.infra.config.RabbitMQConfig;
import br.com.medical.notificationservice.infra.dto.NotificationMessageDTO;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailConsumer {

    private final Logger log = LoggerFactory.getLogger(EmailConsumer.class.getName());
    private final SendEmailNotificationService emailService;

    public EmailConsumer(SendEmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues= RabbitMQConfig.EMAIL_QUEUE, containerFactory = "manualAckListenerContainerFactory")
    public void notificationConsumer(NotificationMessageDTO notificationDTO,
                                     Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        int maxRetries = 3;
        long backoff = 1000;
        var consultation = notificationDTO.consultation();
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                emailService.sendConsultationNotification(notificationDTO.patientEmail(),
                        notificationDTO.patientName(), consultation.doctorName(),
                        consultation.medicalSpecialty(), consultation.consultationDate());
                channel.basicAck(deliveryTag, false);
                return;
            } catch (Exception e) {
                log.warn("Attempt {} failed for delivery {}", attempt + 1, deliveryTag, e);
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        channel.basicNack(deliveryTag, false, false);
                        return;
                    }
                }
            }
        }
        channel.basicNack(deliveryTag, false, false);
    }
}
