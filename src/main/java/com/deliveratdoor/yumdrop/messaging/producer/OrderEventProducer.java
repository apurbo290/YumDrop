package com.deliveratdoor.yumdrop.messaging.producer;

import com.deliveratdoor.yumdrop.config.RabbitMQConfig;
import com.deliveratdoor.yumdrop.messaging.event.OrderAcceptedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderAccepted(OrderAcceptedEvent event) {
        System.out.println("Publishing event...");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                event
        );
    }
}

