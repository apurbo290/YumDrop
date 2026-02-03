package com.deliveratdoor.yumdrop.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long restaurantId;
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    private String paymentMethod;
    private String userId;
}

