package com.deliveratdoor.yumdrop.repositories.resturant;

import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuEntity, Long> {

    List<MenuEntity> findByRestaurantId(Long restaurantId);
}

