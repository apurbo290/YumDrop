package com.deliveratdoor.yumdrop.service.resturantService;

import com.deliveratdoor.yumdrop.dto.resturant.CreateMenuItemRequest;
import com.deliveratdoor.yumdrop.dto.resturant.CreateRestaurantRequest;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.repositories.resturant.MenuItemRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantEntity createRestaurant(CreateRestaurantRequest request) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setOpen(request.isOpen());
        restaurant.setRating(0.0);

        return restaurantRepository.save(restaurant);
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public MenuEntity addMenuItem(Long restaurantId, CreateMenuItemRequest request) {

        RestaurantEntity restaurant = getRestaurant(restaurantId);

        MenuEntity item = new MenuEntity();
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setAvailable(request.isAvailable());
        item.setRestaurant(restaurant);

        return menuItemRepository.save(item);
    }

    public List<MenuEntity> getMenu(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }
}

