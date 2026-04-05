package com.deliveratdoor.yumdrop.messaging.consumer;

import com.deliveratdoor.yumdrop.config.RabbitMQConfig;
import com.deliveratdoor.yumdrop.messaging.event.OrderAcceptedEvent;
import com.deliveratdoor.yumdrop.service.notificationService.EmailNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {

    private final EmailNotificationService emailNotificationService;

    public OrderEventListener(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderAccepted(OrderAcceptedEvent event) {
        log.info("Order accepted event received for order ID: {}", event.getOrderId());
        emailNotificationService.notifyByEmail(event.getUserId());
    }
}

