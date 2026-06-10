package com.gubee.stockreconciliation.adapters.outbound.persistence.repository;

import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockEventRepository extends JpaRepository<StockEvent, Long> {

    boolean existsByEventId(String eventId);

    List<StockEvent> findByStatus(EventStatus status);

    List<StockEvent> findByAccountIdAndSku(String accountId, String sku);
}
