package com.deliveratdoor.yumdrop.repositories.delivery;

import com.deliveratdoor.yumdrop.entity.delivery.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {

    boolean existsByOrderId(Long orderId);
    Optional<DeliveryEntity> findByOrderId(Long orderId);
}

