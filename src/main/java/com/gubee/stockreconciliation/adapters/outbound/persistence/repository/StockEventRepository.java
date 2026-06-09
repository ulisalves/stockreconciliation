package com.gubee.stockreconciliation.adapters.outbound.persistence.repository;

import com.gubee.stockreconciliation.domain.model.StockEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockEventRepository extends JpaRepository<StockEvent, Long> {
}
