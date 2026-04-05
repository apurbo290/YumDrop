package com.deliveratdoor.yumdrop.service.deliveryService;

import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.exception.BadRequestException;
import com.deliveratdoor.yumdrop.exception.InvalidStateException;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.model.PartnerStatus;
import com.deliveratdoor.yumdrop.repositories.delivery.DeliveryRepository;
import com.deliveratdoor.yumdrop.repositories.deliveryPartner.DeliveryPartnerRepository;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import com.deliveratdoor.yumdrop.service.orderService.OrderService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final DeliveryPartnerRepository partnerRepository;
    private final OrderService orderService;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           OrderRepository orderRepository,
                           DeliveryPartnerRepository partnerRepository,
                           @Lazy OrderService orderService) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.partnerRepository = partnerRepository;
        this.orderService = orderService;
    }

    @Transactional
    public DeliveryEntity assignDelivery(Long orderId, Long deliveryPartnerId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new InvalidStateException("Delivery can only be assigned to ACCEPTED orders");
        }
        if (deliveryRepository.existsByOrderId(orderId)) {
            throw new BadRequestException("Delivery already assigned for this order");
        }

        // Point 3: flip partner to ON_DELIVERY
        DeliveryPartnerEntity partner = partnerRepository.findById(deliveryPartnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner not found"));
        if (partner.getStatus() != PartnerStatus.AVAILABLE) {
            throw new InvalidStateException("Partner is not available for delivery");
        }
        partner.setStatus(PartnerStatus.ON_DELIVERY);
        partnerRepository.save(partner);

        // Point 2: sync order to DISPATCHED
        orderService.syncStatusFromDelivery(orderId, OrderStatus.DISPATCHED, "SYSTEM");

        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(orderId);
        delivery.setDeliveryPartnerId(deliveryPartnerId);
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public DeliveryEntity updateStatus(Long deliveryId, DeliveryStatus newStatus) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));

        validateStatusTransition(delivery.getStatus(), newStatus);
        DeliveryStatus prev = delivery.getStatus();
        delivery.setStatus(newStatus);
        deliveryRepository.save(delivery);

        // Point 2: sync order status based on delivery status
        if (newStatus == DeliveryStatus.DELIVERED) {
            orderService.syncStatusFromDelivery(delivery.getOrderId(), OrderStatus.DELIVERED, "SYSTEM");
            // Point 3: flip partner back to AVAILABLE
            setPartnerStatus(delivery.getDeliveryPartnerId(), PartnerStatus.AVAILABLE);
        } else if (newStatus == DeliveryStatus.FAILED) {
            // Order goes back to ACCEPTED so it can be re-dispatched
            orderService.syncStatusFromDelivery(delivery.getOrderId(), OrderStatus.ACCEPTED, "SYSTEM");
            // Point 3: flip partner back to AVAILABLE
            setPartnerStatus(delivery.getDeliveryPartnerId(), PartnerStatus.AVAILABLE);
        }

        return delivery;
    }

    public DeliveryEntity getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for order"));
    }

    private void setPartnerStatus(Long partnerId, PartnerStatus status) {
        partnerRepository.findById(partnerId).ifPresent(p -> {
            p.setStatus(status);
            partnerRepository.save(p);
        });
    }

    private void validateStatusTransition(DeliveryStatus current, DeliveryStatus next) {
        if (current == DeliveryStatus.DELIVERED || current == DeliveryStatus.FAILED) {
            throw new InvalidStateException("Delivery in terminal state: " + current);
        }
        if (current == DeliveryStatus.ASSIGNED && next != DeliveryStatus.PICKED_UP) {
            throw new InvalidStateException("ASSIGNED can only transition to PICKED_UP");
        }
        if (current == DeliveryStatus.PICKED_UP
                && next != DeliveryStatus.DELIVERED
                && next != DeliveryStatus.FAILED) {
            throw new InvalidStateException("PICKED_UP can only transition to DELIVERED or FAILED");
        }
    }
}

