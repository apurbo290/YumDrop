package com.deliveratdoor.yumdrop.repositories.resturant;

import com.deliveratdoor.yumdrop.entity.restaurant.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
