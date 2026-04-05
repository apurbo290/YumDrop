package com.deliveratdoor.yumdrop.dto.order;

import com.deliveratdoor.yumdrop.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderStatusHistoryResponse {
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private String changedBy;
    private LocalDateTime changedAt;
}
