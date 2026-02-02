package com.deliveratdoor.yumdrop.controler.feedbackControler;

import com.deliveratdoor.yumdrop.entity.feedback.FeedbackEntity;
import com.deliveratdoor.yumdrop.service.feedbackService.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // Submit feedback (user)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackEntity submitFeedback(@RequestBody FeedbackEntity feedback) {
        return feedbackService.submitFeedback(feedback);
    }

    // Get feedback by order
    @GetMapping("/order/{orderId}")
    public FeedbackEntity getFeedbackByOrder(@PathVariable Long orderId) {
        return feedbackService.getByOrder(orderId);
    }
}

