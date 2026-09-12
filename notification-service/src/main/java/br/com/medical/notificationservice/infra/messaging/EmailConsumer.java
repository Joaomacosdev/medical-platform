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
        try {
            var consultation = notificationDTO.consultation();
            emailService.sendConsultationNotification(notificationDTO.patientEmail(),
                    consultation.doctorName(), consultation.medicalSpecialty(),
                    consultation.consultationDate());
        } catch (Exception e) {
            log.error("Error while processing notification delivery {}", deliveryTag, e);
            channel.basicNack(deliveryTag, false, false);
            return;
        }
        channel.basicAck(deliveryTag,false);
    }
}
