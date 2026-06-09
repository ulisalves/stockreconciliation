package com.gubee.stockreconciliation.domain.model;

import com.gubee.stockreconciliation.domain.enums.OrderStatus;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLifecycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String orderId;

    private String accountId;

    private String sku;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


}
