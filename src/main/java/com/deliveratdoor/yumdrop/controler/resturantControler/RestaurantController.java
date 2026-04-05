package com.deliveratdoor.yumdrop.controler.resturantControler;

import com.deliveratdoor.yumdrop.dto.resturant.*;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.service.resturantService.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return toDto(restaurantService.createRestaurant(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse updateRestaurant(
            @Valid @RequestBody UpdateRestaurantRequest request, @PathVariable Long id) {
        return toDto(restaurantService.updateRestaurant(request, id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public RestaurantResponse updateRestaurantStatus(
            @Valid @RequestBody UpdateRestaurantRequest request, @PathVariable Long id) {
        return toDto(restaurantService.updateRestaurant(request, id));
    }

    @GetMapping
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantService.getAllRestaurants().stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurant(@PathVariable Long id) {
        return toDto(restaurantService.getRestaurant(id));
    }

    @PostMapping("/{id}/menu")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MenuItemResponse> addMenuItem(
            @PathVariable Long id, @Valid @RequestBody List<CreateMenuItemRequest> request) {
        return restaurantService.addMenuItem(id, request).stream().map(this::toMenuDto).toList();
    }

    @GetMapping("/{id}/menu")
    public List<MenuItemResponse> getMenu(@PathVariable Long id) {
        return restaurantService.getMenu(id).stream().map(this::toMenuDto).toList();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurantById(@PathVariable Long id) {  // fixed: was missing @PathVariable
        restaurantService.deleteRestaurantById(id);
    }

    private RestaurantResponse toDto(RestaurantEntity e) {
        return RestaurantResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .address(e.getAddress())
                .isOpen(e.isOpen())
                .rating(e.getRating())
                .build();
    }

    private MenuItemResponse toMenuDto(MenuEntity e) {
        return MenuItemResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .price(e.getPrice())
                .available(e.isAvailable())
                .build();
    }
}

