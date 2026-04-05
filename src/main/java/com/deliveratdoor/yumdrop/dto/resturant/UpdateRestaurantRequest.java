package com.deliveratdoor.yumdrop.dto.resturant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRestaurantRequest {
    @NotBlank
    private String name;
    private String address;
    private Boolean isOpen;
}
