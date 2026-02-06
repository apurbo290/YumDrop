package com.deliveratdoor.yumdrop.controler.deliveryPartner;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.CreatePartnerRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.DeliveryPartnerResponse;
import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;
import com.deliveratdoor.yumdrop.service.deliveryPartner.DeliveryPartnerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/partners")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @PostMapping
    public DeliveryPartnerEntity create(@Valid @RequestBody CreatePartnerRequest request) {
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
    public PageResponse<DeliveryPartnerResponse> availablePartners(@ModelAttribute PaginationRequest request) {
        return deliveryPartnerService.getAvailablePartners(request);
    }

    @GetMapping("/{id}")
    public DeliveryPartnerEntity getById(@PathVariable Long id) {
        return deliveryPartnerService.getById(id);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteDeliveryPartnerById(@PathVariable Long id) {
        deliveryPartnerService.delete(id);
    }
}

