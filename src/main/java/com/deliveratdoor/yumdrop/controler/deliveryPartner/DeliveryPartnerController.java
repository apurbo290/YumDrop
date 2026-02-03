package com.deliveratdoor.yumdrop.controler.deliveryPartner;

import com.deliveratdoor.yumdrop.dto.deliveryPartner.CreatePartnerRequest;
import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;
import com.deliveratdoor.yumdrop.service.deliveryPartner.DeliveryPartnerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @PostMapping
    public DeliveryPartnerEntity create(@RequestBody CreatePartnerRequest request) {
        return deliveryPartnerService.create(request);
    }

    @PutMapping("/{id}")
    public DeliveryPartnerEntity update(
            @RequestBody CreatePartnerRequest request,
            @PathVariable Long id) {
        return deliveryPartnerService.update(id, request);
    }

    @PutMapping("/{id}/status")
    public DeliveryPartnerEntity updateStatus(
            @PathVariable Long id,
            @RequestBody CreatePartnerRequest request) {
        return deliveryPartnerService.update(id, request);
    }

    @GetMapping("/available")
    public List<DeliveryPartnerEntity> availablePartners() {
        return deliveryPartnerService.getAvailablePartners();
    }

    @GetMapping("/{id}")
    public DeliveryPartnerEntity getById(@PathVariable Long id) {
        return deliveryPartnerService.getById(id);
    }
}

