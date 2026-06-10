package com.gubee.stockreconciliation.controller;

import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockEventRepository;
import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockRepository;
import com.gubee.stockreconciliation.domain.model.Stock;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks")
public class StockController {

    private final StockRepository stockRepository;
    private final StockEventRepository stockEventRepository;

    @Operation(summary = "Get current stock")
    @GetMapping("/{accountId}/{sku}")
    public ResponseEntity<Stock> getStock(@PathVariable String accountId, @PathVariable String sku) {
        return stockRepository
                .findByAccountIdAndSku(accountId, sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get stock history")
    @GetMapping("/{accountId}/{sku}/history")
    public ResponseEntity<List<StockEvent>> history(@PathVariable String accountId, @PathVariable String sku) {
        return ResponseEntity.ok(stockEventRepository.findByAccountIdAndSku(accountId, sku));
    }

}
