package com.deliveratdoor.yumdrop.service.resturantService;

import com.deliveratdoor.yumdrop.dto.resturant.CreateMenuItemRequest;
import com.deliveratdoor.yumdrop.dto.resturant.CreateRestaurantRequest;
import com.deliveratdoor.yumdrop.dto.resturant.UpdateRestaurantRequest;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.repositories.resturant.MenuItemRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public RestaurantEntity updateRestaurant(UpdateRestaurantRequest request, Long id) {
        RestaurantEntity updatedRestaurant = getRestaurant(id);
        if (Objects.nonNull(request.getName())) updatedRestaurant.setName(request.getName());
        if (Objects.nonNull(request.getAddress())) updatedRestaurant.setAddress(request.getAddress());
        if (Objects.nonNull(request.getIsOpen())) updatedRestaurant.setOpen(request.getIsOpen());
        if (Objects.nonNull(request.getRating())) updatedRestaurant.setRating(request.getRating());

        return restaurantRepository.save(updatedRestaurant);
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<MenuEntity> addMenuItem(Long restaurantId, List<CreateMenuItemRequest> requests) {

        RestaurantEntity restaurant = getRestaurant(restaurantId);

        List<MenuEntity> allAddedMenu = new ArrayList<>();

        requests.forEach(request -> {
            MenuEntity item = new MenuEntity();
            item.setName(request.getName());
            item.setPrice(request.getPrice());
            item.setAvailable(request.isAvailable());
            item.setRestaurant(restaurant);
            allAddedMenu.add(item);
        });

        return menuItemRepository.saveAll(allAddedMenu);
    }

    public List<MenuEntity> getMenu(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }
}

