package br.com.medical.notificationservice.infra.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "medical_exchange";
    public static final String EMAIL_QUEUE =  "email_queue";
    public static final String ROUTING_KEY_NOTIFICATION = "medical.notification";
    public static final String EXCHANGE_DQL_NAME = "dlq_medical_exchange";
    public static final String ROUTING_KEY_DLQ_NOTIFICATION = "dlq.notification";
    public static final String DQL_QUEUE = "dql_queue";


    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .deadLetterExchange(EXCHANGE_DQL_NAME)
                .deadLetterRoutingKey(ROUTING_KEY_DLQ_NOTIFICATION)
                .build();
    }
    @Bean
    public Binding emailBinding(Queue emailQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(emailQueue).to(fanoutExchange);
    }

    @Bean
    public DirectExchange dlqExchange() {return new DirectExchange(EXCHANGE_DQL_NAME);}

    @Bean
    public Queue dlqQueue() {return new Queue(DQL_QUEUE, true);}

    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange){
        return BindingBuilder.bind(dlqQueue).to(dlqExchange).with(ROUTING_KEY_DLQ_NOTIFICATION);
    }


    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Value("${spring.rabbitmq.listener.simple.auto-startup:true}") boolean autoStartup) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAutoStartup(autoStartup);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

}
