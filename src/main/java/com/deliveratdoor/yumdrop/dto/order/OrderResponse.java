package com.deliveratdoor.yumdrop.dto.order;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String restaurantName;
    private List<OrderItemResponse> items;
    private String deliveryAddress;
    private String paymentMethod;
    private String orderStatus;
    private double orderAmount;
}
