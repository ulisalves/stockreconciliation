package com.gubee.stockreconciliation.application.service;

import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.OrderLifecycleRepository;
import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockEventRepository;
import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockRepository;
import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.enums.EventType;
import com.gubee.stockreconciliation.domain.enums.OrderStatus;
import com.gubee.stockreconciliation.domain.model.OrderLifecycle;
import com.gubee.stockreconciliation.domain.model.Stock;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final StockEventRepository stockEventRepository;
    private final OrderLifecycleRepository orderLifecycleRepository;
    private final StockRepository stockRepository;

    public void process(CreateEventRequest request) {
        // Idempotência
        if (stockEventRepository.existsByEventId(request.getEventId())) {
            return;
        }
        // Cria evento
        StockEvent event = new StockEvent();
        event.setEventId(request.getEventId());
        event.setType(request.getType());
        event.setAccountId(request.getAccountId());
        event.setSku(request.getSku());
        event.setOccurredAt(request.getOccurredAt());
        event.setProcessedAt(Instant.now());
        event.setStatus(EventStatus.PENDING);
        stockEventRepository.save(event);

        // Busca pedido
        Optional<OrderLifecycle> orderOpt = orderLifecycleRepository.findByOrderId(request.getOrderId());
        OrderLifecycle order;

        if (orderOpt.isPresent()) {
            order = orderOpt.get();
        } else {
            order = new OrderLifecycle();
            order.setOrderId(request.getOrderId());
            order.setAccountId(request.getAccountId());
            order.setSku(request.getSku());
            order.setQuantity(request.getQuantity());
        }

        // =====================================================
        // ORDER_CREATED
        // =====================================================
        if (request.getType() == EventType.ORDER_CREATED) {
            order.setStatus(OrderStatus.CREATED);
            orderLifecycleRepository.save(order);
        }

        // =====================================================
        // ORDER_CANCELLED
        // =====================================================

        if (request.getType() == EventType.ORDER_CANCELLED) {
            // Evento fora de ordem
            if (orderOpt.isEmpty()) {
                event.setStatus(EventStatus.PENDING);
                stockEventRepository.save(event);
                return;
            }
            // Cancelamento duplicado
            if (order.getStatus() == OrderStatus.CANCELLED) {
                event.setStatus(EventStatus.IGNORED);
                stockEventRepository.save(event);
                return;
            }
            order.setStatus(OrderStatus.CANCELLED);
            orderLifecycleRepository.save(order);
        }

        // =====================================================
        // ESTOQUE
        // =====================================================
        Optional<Stock> stockOpt = stockRepository.findByAccountIdAndSku(request.getAccountId(), request.getSku());

        if (stockOpt.isEmpty()) {
            Stock stock = new Stock();
            stock.setAccountId(request.getAccountId());
            stock.setSku(request.getSku());

            if (request.getType() == EventType.ORDER_CREATED) {
                stock.setAvailableQuantity(-request.getQuantity());
            }
            if (request.getType() == EventType.ORDER_CANCELLED) {
                stock.setAvailableQuantity(request.getQuantity());
            }
            stockRepository.save(stock);

        } else {
            Stock stock = stockOpt.get();
            if (request.getType() == EventType.ORDER_CREATED) {
                stock.setAvailableQuantity(stock.getAvailableQuantity() - request.getQuantity());
            }
            if (request.getType() == EventType.ORDER_CANCELLED) {
                stock.setAvailableQuantity(stock.getAvailableQuantity() + request.getQuantity());
            }
            stockRepository.save(stock);
        }
        event.setStatus(EventStatus.PROCESSED);
        stockEventRepository.save(event);

    }
}
