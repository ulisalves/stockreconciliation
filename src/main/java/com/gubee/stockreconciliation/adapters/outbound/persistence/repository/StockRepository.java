package com.gubee.stockreconciliation.adapters.outbound.persistence.repository;

import com.gubee.stockreconciliation.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
