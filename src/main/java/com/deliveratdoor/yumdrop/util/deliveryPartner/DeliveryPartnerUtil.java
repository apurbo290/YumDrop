package com.deliveratdoor.yumdrop.util.deliveryPartner;

import com.deliveratdoor.yumdrop.dto.deliveryPartner.DeliveryPartnerResponse;
import com.deliveratdoor.yumdrop.entity.deliveryPartner.DeliveryPartnerEntity;

import java.util.List;

public class DeliveryPartnerUtil {

    public static List<DeliveryPartnerResponse> mapToDto(List<DeliveryPartnerEntity> entities) {
        return entities.stream()
                .map(entity -> {
                    return DeliveryPartnerResponse.builder()
                            .id(entity.getId())
                            .name(entity.getName())
                            .phone(entity.getPhone())
                            .email(entity.getEmail())
                            .status(entity.getStatus())
                            .active(entity.getActive())
                            .createdAt(entity.getCreatedAt())
                            .updatedAt(entity.getUpdatedAt())
                            .rating(entity.getRating())
                            .build();
                })
                .toList();
    }

}
