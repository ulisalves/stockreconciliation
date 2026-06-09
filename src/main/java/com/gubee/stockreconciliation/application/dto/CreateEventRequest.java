package com.gubee.stockreconciliation.application.dto;

import com.gubee.stockreconciliation.domain.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateEventRequest {
    private String eventId;

    private EventType type;

    private String accountId;

    private String sku;

    private Instant occurredAt;

    private String orderId;

    private Integer quantity;
}
