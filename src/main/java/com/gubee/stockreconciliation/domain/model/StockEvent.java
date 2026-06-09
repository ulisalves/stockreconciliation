package com.gubee.stockreconciliation.domain.model;

import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StockEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String eventId;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String accountId;

    private String sku;

    private Instant occurredAt;

    private Instant processedAt;
}
