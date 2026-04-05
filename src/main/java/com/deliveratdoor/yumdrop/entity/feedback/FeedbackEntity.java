package com.deliveratdoor.yumdrop.entity.feedback;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "feedbacks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "orderId")
        })
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private Long deliveryPartnerId;

    private Integer restaurantRating; // 1–5
    private Integer deliveryRating;   // 1–5

    @Column(length = 100)
    private String comments;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

