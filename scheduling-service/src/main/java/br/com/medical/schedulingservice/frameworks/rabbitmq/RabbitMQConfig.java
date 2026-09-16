package br.com.medical.schedulingservice.frameworks.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange.medical-exchange}")
    private String medicalExchangeName;

    @Value("${app.rabbitmq.exchange.dlq-medical-exchange}")
    private String dlqMedicalExchangeName;

    @Value("${app.rabbitmq.queue.email-queue}")
    private String emailQueueName;

    @Value("${app.rabbitmq.queue.dlq-queue}")
    private String dlqQueueName;

    @Value("${app.rabbitmq.routing-key.dlq-notification}")
    private String dlqRoutingKey;

    @Bean
    public FanoutExchange medicalExchange() {
        return new FanoutExchange(medicalExchangeName, true, false);
    }

    @Bean
    public DirectExchange dlqMedicalExchange() {
        return new DirectExchange(dlqMedicalExchangeName, true, false);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueueName)
                .withArgument("x-dead-letter-exchange", dlqMedicalExchangeName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqQueueName).build();
    }

    @Bean
    public Binding emailQueueBinding() {
        return BindingBuilder.bind(emailQueue()).to(medicalExchange());
    }

    @Bean
    public Binding dlqQueueBinding() {
        return BindingBuilder.bind(dlqQueue()).to(dlqMedicalExchange()).with(dlqRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}