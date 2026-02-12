package com.deliveratdoor.yumdrop.service.orderService;

import com.deliveratdoor.yumdrop.common.pagination.PageResponse;
import com.deliveratdoor.yumdrop.common.pagination.PaginationRequest;
import com.deliveratdoor.yumdrop.dto.order.CreateOrderRequest;
import com.deliveratdoor.yumdrop.dto.order.OrderItemRequest;
import com.deliveratdoor.yumdrop.dto.order.OrderResponse;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import com.deliveratdoor.yumdrop.entity.order.OrderItemEntity;
import com.deliveratdoor.yumdrop.entity.restaurant.MenuEntity;
import com.deliveratdoor.yumdrop.model.OrderStatus;
import com.deliveratdoor.yumdrop.repositories.orders.OrderRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.MenuItemRepository;
import com.deliveratdoor.yumdrop.repositories.resturant.RestaurantRepository;
import com.deliveratdoor.yumdrop.util.orders.OrderUtil;
import com.deliveratdoor.yumdrop.util.pagination.PaginationUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public OrderEntity placeOrder(CreateOrderRequest request) {

        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getItems())) {
            throw new IllegalArgumentException("Order items cannot be empty");
        }

        OrderEntity order = new OrderEntity();
        order.setRestaurant(
                restaurantRepository.findById(request.getRestaurantId())
                        .orElseThrow(() -> new RuntimeException("Restaurant not found"))
        );
        order.setUserId(request.getUserId());
        order.setPaymentMethod(request.getPaymentMethod());
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

    public PageResponse<OrderResponse> getAllOrdersForCurrentUser(PaginationRequest request, String userId) {
        Pageable pageable = PaginationUtil.toPageable(request);
        Page<OrderEntity> page =
                orderRepository.getAllOrdersByUserId(userId, pageable);

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

