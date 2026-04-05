package com.deliveratdoor.yumdrop.dto.delivery;

import com.deliveratdoor.yumdrop.model.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private Long deliveryPartnerId;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime updatedAt;
}
