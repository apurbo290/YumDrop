package com.deliveratdoor.yumdrop.controler.deliveryControler;

import com.deliveratdoor.yumdrop.dto.delivery.DeliveryResponse;
import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import com.deliveratdoor.yumdrop.service.deliveryService.DeliveryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public DeliveryResponse assignDelivery(
            @RequestParam Long orderId,
            @RequestParam Long deliveryPartnerId) {
        return toDto(deliveryService.assignDelivery(orderId, deliveryPartnerId));
    }

    @PostMapping("/{deliveryId}/status")
    @PreAuthorize("hasRole('DELIVERY_PARTNER')")
    public DeliveryResponse updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status) {
        return toDto(deliveryService.updateStatus(deliveryId, status));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DELIVERY_PARTNER')")
    public DeliveryResponse getDeliveryByOrder(@PathVariable Long orderId) {
        return toDto(deliveryService.getByOrderId(orderId));
    }

    private DeliveryResponse toDto(DeliveryEntity e) {
        return DeliveryResponse.builder()
                .id(e.getId())
                .orderId(e.getOrderId())
                .deliveryPartnerId(e.getDeliveryPartnerId())
                .status(e.getStatus())
                .assignedAt(e.getAssignedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}

