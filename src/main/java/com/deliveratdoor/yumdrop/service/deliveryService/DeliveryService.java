package com.deliveratdoor.yumdrop.service.deliveryService;

import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import com.deliveratdoor.yumdrop.repositories.delivery.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public DeliveryEntity assignDelivery(Long orderId, Long deliveryPartnerId) {
        DeliveryEntity delivery = new DeliveryEntity();
        delivery.setOrderId(orderId);
        delivery.setDeliveryPartnerId(deliveryPartnerId);
        return deliveryRepository.save(delivery);
    }

    public DeliveryEntity updateStatus(Long deliveryId, DeliveryStatus status) {
        DeliveryEntity delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(status);
        return deliveryRepository.save(delivery);
    }

    public DeliveryEntity getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order"));
    }
}

