package com.deliveratdoor.yumdrop.service.orderService;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.order.*;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderItemEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderStatusHistoryEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.exception.BadRequestException;
import com.deliveratdoor.yumdrop.exception.InvalidStateException;
import com.deliveratdoor.yumdrop.exception.ResourceNotFoundException;
import com.deliveratdoor.yumdrop.messaging.event.OrderAcceptedEvent;
import com.deliveratdoor.yumdrop.messaging.producer.OrderEventProducer;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import com.deliveratdoor.yumdrop.repositories.orders.OrderStatusHistoryRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.MenuItemRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.RestaurantRepository;
import com.deliveratdoor.yumdrop.util.orders.OrderUtil;
import com.deliveratdoor.yumdrop.util.pagination.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderEventProducer orderEventProducer;
    private final OrderStatusHistoryRepository historyRepository;

    public OrderService(OrderRepository orderRepository,
                        MenuItemRepository menuRepository,
                        RestaurantRepository restaurantRepository,
                        OrderEventProducer orderEventProducer,
                        OrderStatusHistoryRepository historyRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderEventProducer = orderEventProducer;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public OrderEntity placeOrder(CreateOrderRequest request) {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getItems())) {
            throw new BadRequestException("Order items cannot be empty");
        }

        OrderEntity order = new OrderEntity();
        order.setRestaurant(
                restaurantRepository.findById(request.getRestaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"))
        );
        order.setUserId(request.getUserId());
        order.setPaymentMethod(request.getPaymentMethod());
        double totalAmount = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuEntity menu = menuRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemRequest.getMenuItemId()));

            if (!menu.getRestaurant().getId().equals(request.getRestaurantId())) {
                throw new BadRequestException("Menu item does not belong to this restaurant");
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
        OrderEntity saved = orderRepository.save(order);
        recordHistory(saved.getId(), null, OrderStatus.PLACED, saved.getUserId());
        return saved;
    }

    public PageResponse<OrderResponse> getAllOrdersForCurrentUser(PaginationRequest request, String userId) {
        Pageable pageable = PaginationUtil.toPageable(request);
        Page<OrderEntity> page = orderRepository.getAllOrdersByUserId(userId, pageable);
        List<OrderResponse> data = OrderUtil.mapToDto(page.getContent());
        return PageResponse.<OrderResponse>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalRecords(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .data(data)
                .build();
    }

    @Transactional
    public OrderEntity acceptOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED);
        OrderStatus prev = order.getStatus();
        order.setStatus(OrderStatus.ACCEPTED);
        OrderEntity saved = orderRepository.save(order);
        recordHistory(saved.getId(), prev, OrderStatus.ACCEPTED, "SYSTEM");
        orderEventProducer.publishOrderAccepted(
                OrderAcceptedEvent.builder()
                        .orderId(saved.getId())
                        .userId(saved.getUserId())
                        .acceptedAt(Instant.now())
                        .build()
        );
        return saved;
    }

    @Transactional
    public OrderEntity rejectOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED);
        OrderStatus prev = order.getStatus();
        order.setStatus(OrderStatus.REJECTED);
        OrderEntity saved = orderRepository.save(order);
        recordHistory(saved.getId(), prev, OrderStatus.REJECTED, "SYSTEM");
        return saved;
    }

    @Transactional
    public OrderEntity cancelOrder(Long orderId) {
        OrderEntity order = getOrder(orderId);
        validateStatus(order, OrderStatus.PLACED, OrderStatus.ACCEPTED);
        OrderStatus prev = order.getStatus();
        order.setStatus(OrderStatus.CANCELLED);
        OrderEntity saved = orderRepository.save(order);
        recordHistory(saved.getId(), prev, OrderStatus.CANCELLED, order.getUserId());
        return saved;
    }

    // Called by DeliveryService when delivery status changes (Point 2)
    @Transactional
    public void syncStatusFromDelivery(Long orderId, OrderStatus newStatus, String changedBy) {
        OrderEntity order = getOrder(orderId);
        OrderStatus prev = order.getStatus();
        order.setStatus(newStatus);
        orderRepository.save(order);
        recordHistory(orderId, prev, newStatus, changedBy);
    }

    public List<OrderStatusHistoryResponse> getOrderHistory(Long orderId) {
        return historyRepository.findByOrderIdOrderByChangedAtAsc(orderId)
                .stream()
                .map(h -> OrderStatusHistoryResponse.builder()
                        .fromStatus(h.getFromStatus())
                        .toStatus(h.getToStatus())
                        .changedBy(h.getChangedBy())
                        .changedAt(h.getChangedAt())
                        .build())
                .toList();
    }

    public OrderEntity getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private void validateStatus(OrderEntity order, OrderStatus... allowed) {
        for (OrderStatus status : allowed) {
            if (order.getStatus() == status) return;
        }
        throw new InvalidStateException("Invalid order state transition from: " + order.getStatus());
    }

    private void recordHistory(Long orderId, OrderStatus from, OrderStatus to, String changedBy) {
        OrderStatusHistoryEntity history = new OrderStatusHistoryEntity();
        history.setOrderId(orderId);
        history.setFromStatus(from);
        history.setToStatus(to);
        history.setChangedBy(changedBy);
        historyRepository.save(history);
    }
}

