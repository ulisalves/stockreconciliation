package com.gubee.stockreconciliation.controller;

import com.gubee.stockreconciliation.application.dto.CreateEventRequest;
import com.gubee.stockreconciliation.application.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody CreateEventRequest request) {

        eventService.process(request);

        return ResponseEntity.accepted().build();
    }
}
