package com.deliveratdoor.yumdrop.controler.deliveryPartner;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.CreatePartnerRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.DeliveryPartnerResponse;
import com.deliveratdoor.yumdrop.service.deliveryPartner.DeliveryPartnerService;
import com.deliveratdoor.yumdrop.util.deliveryPartner.DeliveryPartnerUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/partners")
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    public DeliveryPartnerController(DeliveryPartnerService deliveryPartnerService) {
        this.deliveryPartnerService = deliveryPartnerService;
    }

    @PostMapping
    public DeliveryPartnerResponse create(@Valid @RequestBody CreatePartnerRequest request) {
        return DeliveryPartnerUtil.mapToDto(List.of(deliveryPartnerService.create(request))).getFirst();
    }

    @PutMapping("/{id}")
    public DeliveryPartnerResponse update(
            @RequestBody CreatePartnerRequest request,
            @PathVariable Long id) {
        return DeliveryPartnerUtil.mapToDto(List.of(deliveryPartnerService.update(id, request))).getFirst();
    }

    @PutMapping("/{id}/status")
    public DeliveryPartnerResponse updateStatus(
            @PathVariable Long id,
            @RequestBody CreatePartnerRequest request) {
        return DeliveryPartnerUtil.mapToDto(List.of(deliveryPartnerService.update(id, request))).getFirst();
    }

    @GetMapping("/available")
    public PageResponse<DeliveryPartnerResponse> availablePartners(@ModelAttribute PaginationRequest request) {
        return deliveryPartnerService.getAvailablePartners(request);
    }

    @GetMapping("/{id}")
    public DeliveryPartnerResponse getById(@PathVariable Long id) {
        return DeliveryPartnerUtil.mapToDto(List.of(deliveryPartnerService.getById(id))).getFirst();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeliveryPartnerById(@PathVariable Long id) {
        deliveryPartnerService.delete(id);
    }
}
