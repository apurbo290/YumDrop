package com.deliveratdoor.yumdrop.dto.order;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long menuItemId;
    private int quantity;
}

