package com.deliveratdoor.yumdrop.service.orderService;


import com.deliveratdoor.yumdrop.dto.order.CreateOrderRequest;
import com.deliveratdoor.yumdrop.dto.order.OrderItemRequest;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderItemEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.MenuItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional
    public OrderEntity placeOrder(CreateOrderRequest request) {

        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getItems())) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        OrderEntity order = new OrderEntity();
        order.setRestaurantId(request.getRestaurantId());
        order.setUserId(request.getUserId());
        double totalAmount = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuEntity menu = menuRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Menu item not found: " + itemRequest.getMenuItemId())
                    );

            if (!menu.getRestaurant().getId().equals(request.getRestaurantId())) {
                throw new IllegalArgumentException("Menu item does not belong to this restaurant");
            }

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menu.getPrice());
            order.getItems().add(orderItem);
            totalAmount += menu.getPrice() * itemRequest.getQuantity();
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }


    public OrderEntity acceptOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED);
        order.setStatus(OrderStatus.ACCEPTED);
        return orderRepository.save(order);
    }

    public OrderEntity rejectOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED);
        order.setStatus(OrderStatus.REJECTED);
        return orderRepository.save(order);
    }

    public OrderEntity cancelOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED, OrderStatus.ACCEPTED);
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public OrderEntity getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("OrderEntity not found"));
    }

    private void validateStatus(OrderEntity order, OrderStatus... allowed) {
        for (OrderStatus status : allowed) {
            if (order.getStatus() == status) {
                return;
            }
        }
        throw new RuntimeException("Invalid order state transition");
    }
}

