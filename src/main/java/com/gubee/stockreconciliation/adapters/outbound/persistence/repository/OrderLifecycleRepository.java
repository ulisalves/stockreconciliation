package com.gubee.stockreconciliation.adapters.outbound.persistence.repository;

import com.gubee.stockreconciliation.domain.model.OrderLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderLifecycleRepository extends JpaRepository<OrderLifecycle, Long> {
    Optional<OrderLifecycle> findByOrderId(String orderId);
}
