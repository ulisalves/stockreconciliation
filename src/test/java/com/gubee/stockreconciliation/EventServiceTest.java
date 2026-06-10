package com.gubee.stockreconciliation;

import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.OrderLifecycleRepository;
import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockEventRepository;
import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockRepository;
import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.application.service.EventService;
import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.enums.EventType;
import com.gubee.stockreconciliation.domain.enums.OrderStatus;
import com.gubee.stockreconciliation.domain.model.OrderLifecycle;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterEach;

import java.time.Instant;
import java.util.Optional;

@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private StockEventRepository stockEventRepository;
    @Autowired

    private OrderLifecycleRepository orderLifecycleRepository;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void cleanup() {
        stockEventRepository.deleteAll();
        orderLifecycleRepository.deleteAll();
        stockRepository.deleteAll();
    }

    @Test
    void shouldIgnoreDuplicatedEvent() {
        CreateEventRequest request = new CreateEventRequest();
        request.setEventId("EVT-TEST-002");
        request.setType(EventType.ORDER_CREATED);
        request.setAccountId("ACCOUNT-1");
        request.setSku("TV001");
        request.setOrderId("ORDER-002");
        request.setQuantity(1);
        request.setOccurredAt(Instant.now());

        eventService.process(request);
        eventService.process(request);

        long count = stockEventRepository.count();
        Assertions.assertEquals(1, count);
    }

    @Test
    void shouldCreateOrder() {
        CreateEventRequest request = new CreateEventRequest();

        request.setEventId("EVT-001");
        request.setType(EventType.ORDER_CREATED);
        request.setAccountId("ACCOUNT-1");
        request.setSku("TV001");
        request.setOrderId("ORDER-001");
        request.setQuantity(2);
        request.setOccurredAt(Instant.now());

        eventService.process(request);

        Optional<OrderLifecycle> order = orderLifecycleRepository.findByOrderId("ORDER-001");

        Assertions.assertTrue(order.isPresent());

        Assertions.assertEquals(OrderStatus.CREATED, order.get().getStatus());
    }

    @Test
    void shouldCancelOrder() {
        CreateEventRequest create = new CreateEventRequest();

        create.setEventId("EVT-003");
        create.setType(EventType.ORDER_CREATED);
        create.setAccountId("ACCOUNT-1");
        create.setSku("TV001");
        create.setOrderId("ORDER-003");
        create.setQuantity(1);
        create.setOccurredAt(Instant.now());

        eventService.process(create);

        CreateEventRequest cancel = new CreateEventRequest();

        cancel.setEventId("EVT-201");
        cancel.setType(EventType.ORDER_CANCELLED);
        cancel.setAccountId("ACCOUNT-1");
        cancel.setSku("TV001");
        cancel.setOrderId("ORDER-003");
        cancel.setQuantity(1);
        cancel.setOccurredAt(Instant.now());

        eventService.process(cancel);

        OrderLifecycle order = orderLifecycleRepository.findByOrderId("ORDER-003").orElseThrow();

        Assertions.assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void shouldMarkPendingWhenOrderNotFound() {
        CreateEventRequest request = new CreateEventRequest();

        request.setEventId("EVT-PENDING-001");
        request.setType(EventType.ORDER_CANCELLED);
        request.setAccountId("ACCOUNT-1");
        request.setSku("TV001");
        request.setOrderId("ORDER-NOT-FOUND");
        request.setQuantity(1);
        request.setOccurredAt(Instant.now());
        eventService.process(request);

        StockEvent event = stockEventRepository.findByEventId("EVT-PENDING-001").orElseThrow();

        Assertions.assertEquals(EventStatus.PENDING, event.getStatus());
    }

    @Test
    void shouldIgnoreDuplicatedCancellation() {
        CreateEventRequest create = new CreateEventRequest();

        create.setEventId("EVT-300");
        create.setType(EventType.ORDER_CREATED);
        create.setAccountId("ACCOUNT-1");
        create.setSku("TV001");
        create.setOrderId("ORDER-300");
        create.setQuantity(1);
        create.setOccurredAt(Instant.now());

        eventService.process(create);

        CreateEventRequest cancel1 = new CreateEventRequest();

        cancel1.setEventId("EVT-301");
        cancel1.setType(EventType.ORDER_CANCELLED);
        cancel1.setAccountId("ACCOUNT-1");
        cancel1.setSku("TV001");
        cancel1.setOrderId("ORDER-300");
        cancel1.setQuantity(1);
        cancel1.setOccurredAt(Instant.now());

        eventService.process(cancel1);

        CreateEventRequest cancel2 = new CreateEventRequest();

        cancel2.setEventId("EVT-302");
        cancel2.setType(EventType.ORDER_CANCELLED);
        cancel2.setAccountId("ACCOUNT-1");
        cancel2.setSku("TV001");
        cancel2.setOrderId("ORDER-300");
        cancel2.setQuantity(1);
        cancel2.setOccurredAt(Instant.now());

        eventService.process(cancel2);

        StockEvent event = stockEventRepository.findByEventId("EVT-302").orElseThrow();

        Assertions.assertEquals(EventStatus.IGNORED, event.getStatus());
    }

    @Test
    void shouldRestoreStockOnlyOnce() {
        CreateEventRequest created = new CreateEventRequest();

        created.setEventId("EVT-004");
        created.setType(EventType.ORDER_CREATED);
        created.setAccountId("ACCOUNT-1");
        created.setSku("TV001");
        created.setOrderId("ORDER-004");
        created.setQuantity(1);
        created.setOccurredAt(Instant.now());
        eventService.process(created);

        CreateEventRequest restored = new CreateEventRequest();

        restored.setEventId("EVT-005");
        restored.setType(EventType.MARKETPLACE_STOCK_RESTORED);
        restored.setAccountId("ACCOUNT-1");
        restored.setSku("TV001");
        restored.setOrderId("ORDER-004");
        restored.setQuantity(1);
        restored.setOccurredAt(Instant.now());

        eventService.process(restored);

        CreateEventRequest duplicated = new CreateEventRequest();

        duplicated.setEventId("EVT-006");
        duplicated.setType(EventType.MARKETPLACE_STOCK_RESTORED);
        duplicated.setAccountId("ACCOUNT-1");
        duplicated.setSku("TV001");
        duplicated.setOrderId("ORDER-004");
        duplicated.setQuantity(1);
        duplicated.setOccurredAt(Instant.now());

        eventService.process(duplicated);

        StockEvent event = stockEventRepository.findByEventId("EVT-006").orElseThrow();

        Assertions.assertEquals(EventStatus.IGNORED, event.getStatus());
    }
}
