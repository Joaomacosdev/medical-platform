package br.com.medical.notificationservice;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.mail.test-connection=false"
})
class NotificationServiceApplicationTests {

    @Autowired
    private RabbitListenerEndpointRegistry listenerRegistry;

    @Test
    void contextLoads() {
        assertFalse(listenerRegistry.getListenerContainers().isEmpty());
        listenerRegistry.getListenerContainers().forEach(container ->
                assertFalse(container.isRunning(), "O teste nao deve iniciar consumidores reais"));
    }

}
