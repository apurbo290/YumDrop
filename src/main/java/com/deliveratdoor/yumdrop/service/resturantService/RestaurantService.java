package com.deliveratdoor.yumdrop.service.resturantService;

import com.deliveratdoor.yumdrop.dto.resturant.CreateMenuItemRequest;
import com.deliveratdoor.yumdrop.dto.resturant.CreateRestaurantRequest;
import com.deliveratdoor.yumdrop.dto.resturant.UpdateRestaurantRequest;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.repositories.feedback.FeedbackRepository;
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
    private final FeedbackRepository feedbackRepository;

    public RestaurantEntity createRestaurant(CreateRestaurantRequest request) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setOpen(request.isOpen());
        restaurant.setRating(0.0);
        return restaurantRepository.save(restaurant);
    }

    public RestaurantEntity updateRestaurant(UpdateRestaurantRequest request, Long id) {
        RestaurantEntity restaurant = getRestaurant(id);
        if (Objects.nonNull(request.getName())) restaurant.setName(request.getName());
        if (Objects.nonNull(request.getAddress())) restaurant.setAddress(request.getAddress());
        if (Objects.nonNull(request.getIsOpen())) restaurant.setOpen(request.getIsOpen());
        return restaurantRepository.save(restaurant);
    }

    // Point 10: rating computed from feedback, not manually set
    public RestaurantEntity recomputeRating(Long restaurantId) {
        RestaurantEntity restaurant = getRestaurant(restaurantId);
        double avg = feedbackRepository.findByRestaurantId(restaurantId)
                .stream()
                .filter(f -> f.getRestaurantRating() != null)
                .mapToInt(f -> f.getRestaurantRating())
                .average()
                .orElse(0.0);
        restaurant.setRating(avg);
        return restaurantRepository.save(restaurant);
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
    }

    public void deleteRestaurantById(Long id) {
        restaurantRepository.delete(
                restaurantRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"))
        );
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

