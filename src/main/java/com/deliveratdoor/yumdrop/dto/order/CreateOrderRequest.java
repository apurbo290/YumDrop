package com.deliveratdoor.yumdrop.dto.order;

import com.deliveratdoor.yumdrop.model.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private Long restaurantId;
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private String userId;
}

