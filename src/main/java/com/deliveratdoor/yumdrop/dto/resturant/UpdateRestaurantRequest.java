package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Data;

@Data
public class UpdateRestaurantRequest {
    private String name;
    private String address;
    private Boolean isOpen;
    private Double rating;
}
