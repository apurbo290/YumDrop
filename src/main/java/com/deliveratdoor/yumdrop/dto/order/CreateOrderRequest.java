package com.deliveratdoor.yumdrop.dto.order;

import com.deliveratdoor.yumdrop.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long restaurantId;
    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
    private String deliveryAddress;
    @NotNull
    private PaymentMethod paymentMethod;
    private String userId;
}

