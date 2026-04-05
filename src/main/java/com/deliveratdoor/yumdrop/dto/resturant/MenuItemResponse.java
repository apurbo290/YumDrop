package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuItemResponse {
    private Long id;
    private String name;
    private double price;
    private boolean available;
}
