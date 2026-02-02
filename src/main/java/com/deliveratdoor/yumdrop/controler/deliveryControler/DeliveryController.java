package com.deliveratdoor.yumdrop.controler.deliveryControler;

import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import com.deliveratdoor.yumdrop.service.deliveryService.DeliveryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // Assign delivery partner to order
    @PostMapping("/assign")
    public DeliveryEntity assignDelivery(
            @RequestParam Long orderId,
            @RequestParam Long deliveryPartnerId) {

        return deliveryService.assignDelivery(orderId, deliveryPartnerId);
    }

    // Update delivery status
    @PostMapping("/{deliveryId}/status")
    public DeliveryEntity updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {

        return deliveryService.updateStatus(deliveryId, status);
    }

    // Get delivery by order
    @GetMapping("/order/{orderId}")
    public DeliveryEntity getDeliveryByOrder(@PathVariable Long orderId) {
        return deliveryService.getByOrderId(orderId);
    }
}

