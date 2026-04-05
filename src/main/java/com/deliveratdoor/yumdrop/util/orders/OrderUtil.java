package com.deliveratdoor.yumdrop.util.orders;

import com.deliveratdoor.yumdrop.dto.order.OrderItemResponse;
import com.deliveratdoor.yumdrop.dto.order.OrderResponse;
import com.deliveratdoor.yumdrop.entity.order.OrderEntity;
import java.util.List;
import java.util.Objects;

public class OrderUtil {
    public static List<OrderResponse> mapToDto(List<OrderEntity> entities) {
        return entities.stream()
                .map(eachOrder -> {
                    List<OrderItemResponse> itemResponses = eachOrder.getItems().stream()
                            .map(item ->
                                OrderItemResponse.builder()
                                        .menuName(item.getMenu().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build()
                            ).toList();

                    return OrderResponse.builder()
                            .restaurantName(eachOrder.getRestaurant().getName())
                            .items(itemResponses)
                            .deliveryAddress(eachOrder.getDeliveryAddress())
                            .paymentMethod(Objects.nonNull(eachOrder.getPaymentMethod())
                                    ? eachOrder.getPaymentMethod().name() : null)
                            .orderStatus(eachOrder.getStatus() != null ? eachOrder.getStatus().name() : null)
                            .orderAmount(eachOrder.getTotalAmount())
                            .build();
                })
                .toList();
    }
}
