package com.deliveratdoor.yumdrop.service.deliveryService;

import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.repositories.delivery.DeliveryRepository;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public DeliveryEntity assignDelivery(Long orderId, Long deliveryPartnerId) {
        // 1. Order must exist
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        // 2. Order must be ACCEPTED
        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new RuntimeException("Delivery can be assigned only to ACCEPTED orders");
        }
        // 3. Order must not already have delivery
        if (deliveryRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Delivery already assigned for this order");
        }

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(orderId);
        delivery.setDeliveryPartnerId(deliveryPartnerId);
        return deliveryRepository.save(delivery);
    }

    public DeliveryEntity updateStatus(Long deliveryId, DeliveryStatus status) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        validateStatusTransition(delivery.getStatus(), status);
        delivery.setStatus(status);
        return deliveryRepository.save(delivery);
    }

    public DeliveryEntity getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order"));
    }

    private void validateStatusTransition(DeliveryStatus current, DeliveryStatus next) {
        if (current == DeliveryStatus.DELIVERED) {
            throw new RuntimeException("Delivered order cannot be updated");
        }
        if (current == DeliveryStatus.ASSIGNED && next != DeliveryStatus.PICKED_UP) {
            throw new RuntimeException("Invalid transition");
        }
        if (current == DeliveryStatus.PICKED_UP && next != DeliveryStatus.DELIVERED) {
            throw new RuntimeException("Invalid transition");
        }
    }

}

