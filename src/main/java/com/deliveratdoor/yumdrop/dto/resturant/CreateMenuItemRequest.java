package com.deliveratdoor.yumdrop.dto.resturant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateMenuItemRequest {
    @NotBlank
    private String name;
    @Positive
    private double price;
    private boolean isAvailable;
}

