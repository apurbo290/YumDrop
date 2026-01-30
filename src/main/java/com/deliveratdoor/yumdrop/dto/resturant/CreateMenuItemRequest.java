package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Data;

@Data
public class CreateMenuItemRequest {
    private String name;
    private double price;
    private boolean isAvailable;
}

