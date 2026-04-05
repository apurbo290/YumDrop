package com.deliveratdoor.yumdrop.entity.deliveryPartner;

import com.deliveratdoor.yumdrop.model.PartnerStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_partners")
@Data
public class DeliveryPartnerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private PartnerStatus status;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double rating;

    @PrePersist
    void onCreate() {
        this.status = PartnerStatus.AVAILABLE;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

