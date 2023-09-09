package com.arthurdream.sporteventapi.controller;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.dto.OnCreate;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import com.arthurdream.sporteventapi.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(@RequestParam(required = false) EventStatus status,
        @RequestParam(required = false) SportType sportType) {
        log.info("Fetching events with filters - Status: {}, Sport Type: {}", status, sportType);
        List<EventDTO> events = eventService.getEvents(status, sportType);
        return ResponseEntity.ok(events);
    }

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        log.info("Fetching event with ID: {}", id);
        Optional<EventDTO> eventDTO = eventService.getEventById(id);
        return eventDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Validated({OnCreate.class, Default.class}) @RequestBody EventDTO eventDTO) {
        log.info("Creating new event with name: {}", eventDTO.getName());
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeEventStatus(@PathVariable Long id, @RequestBody EventStatus newStatus) {
        log.info("Changing status of event with ID {} to {}", id, newStatus);
        boolean statusChanged = eventService.changeEventStatus(id, newStatus);
        if (statusChanged) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
