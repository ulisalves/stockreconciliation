package com.gubee.stockreconciliation.application.service;

import com.gubee.stockreconciliation.adapters.outbound.persistence.repository.StockEventRepository;
import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventService {

    private final StockEventRepository stockEventRepository;

    public void process(CreateEventRequest request) {

        if (stockEventRepository.existsByEventId(request.getEventId())) {
            return;
        }

        StockEvent event = new StockEvent();

        event.setEventId(request.getEventId());
        event.setType(request.getType());
        event.setAccountId(request.getAccountId());
        event.setSku(request.getSku());
        event.setOccurredAt(request.getOccurredAt());
        event.setProcessedAt(Instant.now());
        event.setStatus(EventStatus.PENDING);

        stockEventRepository.save(event);
    }
}
