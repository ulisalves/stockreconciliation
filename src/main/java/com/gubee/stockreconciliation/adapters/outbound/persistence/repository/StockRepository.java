package com.gubee.stockreconciliation.adapters.outbound.persistence.repository;

import com.gubee.stockreconciliation.domain.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByAccountIdAndSku(String accountId, String sku);
}
