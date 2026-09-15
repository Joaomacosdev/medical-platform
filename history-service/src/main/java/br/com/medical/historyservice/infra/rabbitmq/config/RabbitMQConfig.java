package br.com.medical.historyservice.infra.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange appointmentEventsExchange(
            @Value("${app.rabbitmq.exchange.appointment-events:appointment.events}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue appointmentEventsQueue(
            @Value("${app.rabbitmq.queue.appointment-events:history.appointment.events}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding appointmentEventsBinding(
            Queue appointmentEventsQueue,
            TopicExchange appointmentEventsExchange,
            @Value("${app.rabbitmq.routing-key.appointment-events:appointment.#}") String routingKey) {
        return BindingBuilder.bind(appointmentEventsQueue).to(appointmentEventsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}