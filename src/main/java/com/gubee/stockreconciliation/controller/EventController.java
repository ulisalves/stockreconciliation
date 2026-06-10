package com.gubee.stockreconciliation.controller;

import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.application.service.EventService;
import com.gubee.stockreconciliation.domain.enums.EventStatus;
import com.gubee.stockreconciliation.domain.model.StockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Process stock event")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateEventRequest request) {
        eventService.process(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<StockEvent>> findByStatus(@RequestParam EventStatus status) {
        return ResponseEntity.ok(eventService.findByStatus(status));
    }
}
