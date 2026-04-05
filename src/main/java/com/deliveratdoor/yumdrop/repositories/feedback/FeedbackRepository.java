package com.deliveratdoor.yumdrop.repositories.feedback;

import com.deliveratdoor.yumdrop.entity.feedback.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    Optional<FeedbackEntity> findByOrderId(Long orderId);

    List<FeedbackEntity> findByRestaurantId(Long restaurantId);

    List<FeedbackEntity> findByDeliveryPartnerId(Long deliveryPartnerId);
}

