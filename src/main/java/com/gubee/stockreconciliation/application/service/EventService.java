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
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final StockEventRepository stockEventRepository;
    private final OrderLifecycleRepository orderLifecycleRepository;
    private final StockRepository stockRepository;

    @Transactional
    public void process(CreateEventRequest request) {

        //logs estruturados - Log início
        log.info(
                "Processing eventId={}, type={}, orderId={}",
                request.getEventId(),
                request.getType(),
                request.getOrderId()
        );

        // Idempotência
        if (stockEventRepository.existsByEventId(request.getEventId())) {
            //logs estruturados - Evento duplicado
            log.warn(
                    "Ignoring duplicated event {}",
                    request.getEventId()
            );
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
                //logs estruturados - Evento pendente
                log.info(
                        "Order {} not found. Event {} marked as PENDING",
                        request.getOrderId(),
                        request.getEventId()
                );
                return;
            }
            // Cancelamento duplicado
            if (order.getStatus() == OrderStatus.CANCELLED) {
                event.setStatus(EventStatus.IGNORED);
                stockEventRepository.save(event);
                //logs estruturados - Evento ignorado
                log.info(
                        "Order {} already cancelled",
                        request.getOrderId()
                );
                return;
            }
            order.setStatus(OrderStatus.CANCELLED);
            orderLifecycleRepository.save(order);
        }

        // =====================================================
        // MARKETPLACE_STOCK_RESTORED
        // =====================================================

        if (request.getType() == EventType.MARKETPLACE_STOCK_RESTORED) {
            if (orderOpt.isEmpty()) {
                event.setStatus(EventStatus.PENDING);
                stockEventRepository.save(event);
                return;
            }

            if (order.getStatus() == OrderStatus.STOCK_RESTORED) {
                //Logs estruturados - Marketplace duplicado
                log.info(
                        "Stock already restored for order {}",
                        request.getOrderId()
                );
                event.setStatus(EventStatus.IGNORED);
                stockEventRepository.save(event);
                return;
            }
            order.setStatus(OrderStatus.STOCK_RESTORED);
            orderLifecycleRepository.save(order);
        }

        if (request.getType() == EventType.STOCK_ADJUSTED) {
            Optional<Stock> stockOpt = stockRepository.findByAccountIdAndSku(request.getAccountId(), request.getSku());
            Stock stock;
            
            if(stockOpt.isPresent()) {
                stock = stockOpt.get();
            } else {
                stock = new Stock();
                stock.setAccountId(request.getAccountId());
                stock.setSku(request.getSku());
            }
            stock.setAvailableQuantity(request.getAvailable());
            stockRepository.save(stock);
            event.setStatus(EventStatus.PROCESSED);
            stockEventRepository.save(event);
            return;
        }

        // =====================================================
        // ESTOQUE
        // =====================================================
        Optional<Stock> stockOpt = stockRepository.findByAccountIdAndSku(request.getAccountId(), request.getSku());

        if (stockOpt.isEmpty()) {
            Stock stock = new Stock();
            stock.setAccountId(request.getAccountId());
            stock.setSku(request.getSku());

            if (request.getType() == EventType.ORDER_CREATED){
                stock.setAvailableQuantity(-request.getQuantity());
            }
            if (request.getType() == EventType.ORDER_CANCELLED || request.getType() == EventType.MARKETPLACE_STOCK_RESTORED) {
                stock.setAvailableQuantity(request.getQuantity());
            }
            stockRepository.save(stock);

        } else {
            Stock stock = stockOpt.get();
            if (request.getType() == EventType.ORDER_CREATED){
                stock.setAvailableQuantity(stock.getAvailableQuantity() - request.getQuantity());
            }
            if (request.getType() == EventType.ORDER_CANCELLED || request.getType() == EventType.MARKETPLACE_STOCK_RESTORED) {
                stock.setAvailableQuantity(stock.getAvailableQuantity() + request.getQuantity());
            }
            stockRepository.save(stock);
        }
        event.setStatus(EventStatus.PROCESSED);
        stockEventRepository.save(event);
    }

    public List<StockEvent> findByStatus(EventStatus status) {
        return stockEventRepository.findByStatus(status);
    }
}
