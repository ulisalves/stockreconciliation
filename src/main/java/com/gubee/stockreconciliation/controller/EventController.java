package com.gubee.stockreconciliation.controller;

import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.application.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Process stock event")
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateEventRequest request) {

        eventService.process(request);

        return ResponseEntity.accepted().build();
    }
}
