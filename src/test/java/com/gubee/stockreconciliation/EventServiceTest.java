package com.gubee.stockreconciliation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EventServiceTest {

    @Test
    void shouldIgnoreDuplicatedEvent() {
        // Lógica para: Deve ignorar evento duplicado
    }

    @Test
    void shouldCreateOrder() {
        // Lógica para: Deve criar pedido
    }

    @Test
    void shouldCancelOrder() {
        // Lógica para: Deve cancelar pedido
    }

    @Test
    void shouldMarkPendingWhenOrderNotFound() {
        // Lógica para: Deve deixar evento pendente
    }

    @Test
    void shouldIgnoreDuplicatedCancellation() {
        // Lógica para: Deve ignorar cancelamento duplicado
    }

    @Test
    void shouldRestoreStockOnlyOnce() {
    }
}
