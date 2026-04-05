package com.deliveratdoor.yumdrop.dto.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateFeedbackRequest {
    @NonNull
    private Long orderId;
    @NonNull
    private Long userId;
    @NonNull
    private Long restaurantId;
    private Long deliveryPartnerId;
    @Min(1)
    @Max(5)
    private Integer restaurantRating;
    @Min(1)
    @Max(5)
    private Integer deliveryRating;
    @Size(min = 5,  max = 100, message = "Comments must be between 5 and 100 characters")
    private String comments;
}
