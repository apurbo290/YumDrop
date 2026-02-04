package com.deliveratdoor.yumdrop.controler.resturantControler;

import com.deliveratdoor.yumdrop.dto.resturant.CreateMenuItemRequest;
import com.deliveratdoor.yumdrop.dto.resturant.CreateRestaurantRequest;
import com.deliveratdoor.yumdrop.dto.resturant.UpdateRestaurantRequest;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.service.resturantService.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PutMapping("/{id}")
    public RestaurantEntity updateRestaurant(
            @RequestBody UpdateRestaurantRequest request, @PathVariable Long id) {
        return restaurantService.updateRestaurant(request, id);
    }

    @PatchMapping("/{id}/status")
    public RestaurantEntity updateRestaurantStatus(
            @RequestBody UpdateRestaurantRequest request, @PathVariable Long id) {
        return restaurantService.updateRestaurant(request, id);
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
    public List<MenuEntity> addMenuItem(
            @PathVariable Long id, @RequestBody List<CreateMenuItemRequest> request) {
        return restaurantService.addMenuItem(id, request);
    }

    @GetMapping("/{id}/menu")
    public List<MenuEntity> getMenu(@PathVariable Long id) {
        return restaurantService.getMenu(id);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteRestaurantById(@PathVariable Long id) {
        restaurantService.deleteRestaurantById(id);
    }
}

