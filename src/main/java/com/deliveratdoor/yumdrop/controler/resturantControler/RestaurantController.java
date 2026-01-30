package com.deliveratdoor.yumdrop.controler.resturantControler;

import com.deliveratdoor.yumdrop.dto.resturant.CreateMenuItemRequest;
import com.deliveratdoor.yumdrop.dto.resturant.CreateRestaurantRequest;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.service.resturantService.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public RestaurantEntity createRestaurant(
            @RequestBody CreateRestaurantRequest request) {
        return restaurantService.createRestaurant(request);
    }

    @GetMapping
    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public RestaurantEntity getRestaurant(@PathVariable Long id) {
        return restaurantService.getRestaurant(id);
    }

    @PostMapping("/{id}/menu")
    public MenuEntity addMenuItem(
            @PathVariable Long id,
            @RequestBody CreateMenuItemRequest request) {
        return restaurantService.addMenuItem(id, request);
    }

    @GetMapping("/{id}/menu")
    public List<MenuEntity> getMenu(@PathVariable Long id) {
        return restaurantService.getMenu(id);
    }
}

