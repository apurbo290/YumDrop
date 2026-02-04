package com.deliveratdoor.yumdrop.service.feedbackService;

import com.deliveratdoor.yumdrop.dto.feedback.CreateFeedbackRequest;
import com.deliveratdoor.yumdrop.entity.feedback.FeedbackEntity;
import com.deliveratdoor.yumdrop.repositories.feedback.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public FeedbackEntity submitFeedback(CreateFeedbackRequest feedback) {

        // One feedback per order
        feedbackRepository.findByOrderId(feedback.getOrderId())
                .ifPresent(f -> {
                    throw new RuntimeException("Feedback already submitted for this order");
                });
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setOrderId(feedback.getOrderId());
        feedbackEntity.setUserId(feedback.getUserId());
        feedbackEntity.setRestaurantId(feedback.getRestaurantId());
        feedbackEntity.setDeliveryPartnerId(feedback.getDeliveryPartnerId());
        feedbackEntity.setRestaurantRating(feedback.getRestaurantRating());
        feedbackEntity.setDeliveryRating(feedback.getDeliveryRating());
        feedbackEntity.setComments(feedback.getComments());

        return feedbackRepository.save(feedbackEntity);
    }

    public FeedbackEntity getByOrder(Long orderId) {
        return feedbackRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }
}

