package com.deliveratdoor.yumdrop.dto.resturant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private boolean isOpen;
    private double rating;
}
