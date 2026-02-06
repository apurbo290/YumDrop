package com.deliveratdoor.yumdrop.dto.deliveryPartner;

import com.deliveratdoor.yumdrop.model.PartnerStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryPartnerResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private PartnerAddress address;
    private double rating;
    private PartnerStatus status;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
