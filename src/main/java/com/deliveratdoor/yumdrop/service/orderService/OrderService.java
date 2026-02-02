package com.deliveratdoor.yumdrop.service.orderService;


import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderEntity placeOrder(OrderEntity order) {
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

    private OrderEntity getOrder(Long orderId) {
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

