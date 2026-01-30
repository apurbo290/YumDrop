package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Data;

@Data
public class CreateRestaurantRequest {
    private String name;
    private String address;
    private boolean isOpen;
}

