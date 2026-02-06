package com.deliveratdoor.yumdrop.service.deliveryPartner;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.CreatePartnerRequest;
import com.deliveratdoor.yumdrop.dto.deliveryPartner.DeliveryPartnerResponse;
import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.model.PartnerStatus;
import com.deliveratdoor.yumdrop.repositories.deliveryPartner.DeliveryPartnerRepository;
import com.deliveratdoor.yumdrop.util.deliveryPartner.DeliveryPartnerUtil;
import com.deliveratdoor.yumdrop.util.pagination.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository repository;

    public DeliveryPartnerService(DeliveryPartnerRepository repository) {
        this.repository = repository;
    }

    public DeliveryPartnerEntity create(CreatePartnerRequest request) {
        DeliveryPartnerEntity partner = new DeliveryPartnerEntity();
        partner.setName(request.getName());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        return repository.save(partner);
    }

    public DeliveryPartnerEntity update(Long id, CreatePartnerRequest request) {
        DeliveryPartnerEntity partner = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));

        if (!isBlank(request.getPhone())) partner.setPhone(request.getPhone());
        if (!isBlank(request.getEmail())) partner.setEmail(request.getEmail());
        if (Objects.nonNull(request.getStatus()) && !isBlank(request.getStatus().name()))
            partner.setStatus(request.getStatus());
        return repository.save(partner);
    }

    public DeliveryPartnerEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));
    }

    public void delete(Long id) {
        DeliveryPartnerEntity partner = getById(id);
        repository.delete(partner);
    }

    public PageResponse<DeliveryPartnerResponse> getAvailablePartners(PaginationRequest request) {
        Pageable pageable = PaginationUtil.toPageable(request);
        Page<DeliveryPartnerEntity> page =
                repository.findByStatusAndActiveTrue(PartnerStatus.AVAILABLE, pageable);

        List<DeliveryPartnerResponse> data = DeliveryPartnerUtil.mapToDto(page.getContent());

        return PageResponse.<DeliveryPartnerResponse>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .data(data)
                .build();
    }
}
