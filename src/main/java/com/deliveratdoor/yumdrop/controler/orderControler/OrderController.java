package com.deliveratdoor.yumdrop.controler.orderControler;

import com.deliveratdoor.yumdrop.dto.order.CreateOrderRequest;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.service.orderService.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // User places order
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public OrderEntity placeOrder(@RequestBody CreateOrderRequest order,
                                  @AuthenticationPrincipal String userId) {
        order.setUserId(userId);
        return orderService.placeOrder(order);
    }

    // Restaurant accepts order
    @PostMapping("/{orderId}/accept")
    public OrderEntity acceptOrder(@PathVariable Long orderId) {
        return orderService.acceptOrder(orderId);
    }

    // Restaurant rejects order
    @PostMapping("/{orderId}/reject")
    public OrderEntity rejectOrder(@PathVariable Long orderId) {
        return orderService.rejectOrder(orderId);
    }

    // User cancels order
    @PostMapping("/{orderId}/cancel")
    public OrderEntity cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER', 'SUPPORT', 'ADMIN')")
    public OrderEntity getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}

