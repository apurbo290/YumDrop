package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Data;
import lombok.NonNull;

@Data
public class CreateMenuItemRequest {
    @NonNull private String name;
    private double price;
    private boolean isAvailable;
}

