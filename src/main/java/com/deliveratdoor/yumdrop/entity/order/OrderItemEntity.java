package com.deliveratdoor.yumdrop.entity.order;

import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which order this item belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // which menu item was ordered
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

    @Column(nullable = false)
    private int quantity;

    // snapshot of price at order time
    @Column(nullable = false)
    private double price;
}

