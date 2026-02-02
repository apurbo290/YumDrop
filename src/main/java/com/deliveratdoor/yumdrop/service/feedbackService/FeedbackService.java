package com.deliveratdoor.yumdrop.service.feedbackService;

import com.deliveratdoor.yumdrop.entity.feedback.FeedbackEntity;
import com.deliveratdoor.yumdrop.repositories.feedback.FeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public FeedbackEntity submitFeedback(FeedbackEntity feedback) {

        // One feedback per order
        feedbackRepository.findByOrderId(feedback.getOrderId())
                .ifPresent(f -> {
                    throw new RuntimeException("Feedback already submitted for this order");
                });

        return feedbackRepository.save(feedback);
    }

    public FeedbackEntity getByOrder(Long orderId) {
        return feedbackRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }
}

