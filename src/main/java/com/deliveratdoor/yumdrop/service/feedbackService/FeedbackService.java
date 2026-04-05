package com.deliveratdoor.yumdrop.service.feedbackService;

import com.deliveratdoor.yumdrop.dto.feedback.CreateFeedbackRequest;
import com.deliveratdoor.yumdrop.entity.feedback.FeedbackEntity;
import com.deliveratdoor.yumdrop.exception.BadRequestException;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.repositories.feedback.FeedbackRepository;
import com.deliveratdoor.yumdrop.service.resturantService.RestaurantService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final RestaurantService restaurantService;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           @Lazy RestaurantService restaurantService) {
        this.feedbackRepository = feedbackRepository;
        this.restaurantService = restaurantService;
    }

    @Transactional
    public FeedbackEntity submitFeedback(CreateFeedbackRequest feedback) {
        feedbackRepository.findByOrderId(feedback.getOrderId())
                .ifPresent(f -> {
                    throw new BadRequestException("Feedback already submitted for this order");
                });

        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setOrderId(feedback.getOrderId());
        feedbackEntity.setUserId(feedback.getUserId());
        feedbackEntity.setRestaurantId(feedback.getRestaurantId());
        feedbackEntity.setDeliveryPartnerId(feedback.getDeliveryPartnerId());
        feedbackEntity.setRestaurantRating(feedback.getRestaurantRating());
        feedbackEntity.setDeliveryRating(feedback.getDeliveryRating());
        feedbackEntity.setComments(feedback.getComments());

        FeedbackEntity saved = feedbackRepository.save(feedbackEntity);

        // Recompute restaurant rating after new feedback
        restaurantService.recomputeRating(feedback.getRestaurantId());

        return saved;
    }

    public FeedbackEntity getByOrder(Long orderId) {
        return feedbackRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found"));
    }
}

