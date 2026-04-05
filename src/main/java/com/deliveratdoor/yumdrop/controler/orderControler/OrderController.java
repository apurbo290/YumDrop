package com.deliveratdoor.yumdrop.controler.orderControler;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.order.CreateOrderRequest;
import com.deliveratdoor.yumdrop.dto.order.OrderResponse;
import com.deliveratdoor.yumdrop.dto.order.OrderStatusHistoryResponse;
import com.deliveratdoor.yumdrop.service.orderService.OrderService;
import com.deliveratdoor.yumdrop.util.orders.OrderUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public OrderResponse placeOrder(@Valid @RequestBody CreateOrderRequest order,
                                    @AuthenticationPrincipal String userId) {
        order.setUserId(userId);
        return OrderUtil.mapToDto(List.of(orderService.placeOrder(order))).getFirst();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PageResponse<OrderResponse> getAllOrdersForCurrentUser(
            @ModelAttribute PaginationRequest request,
            @AuthenticationPrincipal String userId) {
        return orderService.getAllOrdersForCurrentUser(request, userId);
    }

    @PostMapping("/{orderId}/accept")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public OrderResponse acceptOrder(@PathVariable Long orderId) {
        return OrderUtil.mapToDto(List.of(orderService.acceptOrder(orderId))).getFirst();
    }

    @PostMapping("/{orderId}/reject")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public OrderResponse rejectOrder(@PathVariable Long orderId) {
        return OrderUtil.mapToDto(List.of(orderService.rejectOrder(orderId))).getFirst();
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public OrderResponse cancelOrder(@PathVariable Long orderId) {
        return OrderUtil.mapToDto(List.of(orderService.cancelOrder(orderId))).getFirst();
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'ADMIN')")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return OrderUtil.mapToDto(List.of(orderService.getOrder(orderId))).getFirst();
    }

    @GetMapping("/{orderId}/history")
    @PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'ADMIN')")
    public List<OrderStatusHistoryResponse> getOrderHistory(@PathVariable Long orderId) {
        return orderService.getOrderHistory(orderId);
    }
}

