package com.deliveratdoor.yumdrop.dto.resturant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRestaurantRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    private boolean isOpen;
}

