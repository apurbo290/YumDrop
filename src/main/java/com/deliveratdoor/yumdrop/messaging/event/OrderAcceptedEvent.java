package com.deliveratdoor.yumdrop.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAcceptedEvent implements Serializable {

    private Long orderId;
    private String userId;
    private Instant acceptedAt;
}

