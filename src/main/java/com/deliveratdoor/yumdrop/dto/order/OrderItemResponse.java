package com.deliveratdoor.yumdrop.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private String menuName;
    private int quantity;
    private double price;
}
