package com.deliveratdoor.yumdrop.repositories.orders;

import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> getAllOrdersByUserId(String userId, Pageable pageable);
}

