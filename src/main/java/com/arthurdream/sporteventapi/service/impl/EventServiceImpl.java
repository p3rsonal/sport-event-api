package com.arthurdream.sporteventapi.service.impl;

import static com.arthurdream.sporteventapi.repository.Utils.EventRepositoryHelper.filterBySportType;
import static com.arthurdream.sporteventapi.repository.Utils.EventRepositoryHelper.filterByStatus;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.mapper.EventMapper;
import com.arthurdream.sporteventapi.model.Event;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import com.arthurdream.sporteventapi.repository.EventRepository;
import com.arthurdream.sporteventapi.service.EventService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventMapper eventMapper, EventRepository eventRepository) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
    }

    @Override
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public EventDTO createEvent(EventDTO eventDTO) {
        log.info("Creating new event with name: {}", eventDTO.getName());

        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);

        log.info("Event with ID {} created successfully", event.getId());

        return eventMapper.toDTO(event);
    }

    @Override
    @Cacheable(value = "events", key = "#root.method.name + '_' + #status + '_' + #sportType")
    public List<EventDTO> getEvents(EventStatus status, SportType sportType) {
        List<Event> events = eventRepository.findAll(getEventSpecification(status, sportType));
        return events.stream().map(eventMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "event", key = "#eventId")
    public Optional<EventDTO> getEventById(Long eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        return event.map(eventMapper::toDTO);
    }

    @Override
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public boolean changeEventStatus(Long eventId, EventStatus newStatus) {
        log.info("Attempting to change the status of event with ID {} to {}", eventId, newStatus);

        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            log.warn("No event found with ID {}", eventId);
            return false;
        }
        validateStatusChange(eventId, newStatus, optionalEvent.get());

        eventRepository.updateStatus(eventId, newStatus);
        log.info("Status for event with ID {} changed to {}", eventId, newStatus);
        return true;
    }

    private void validateStatusChange(Long eventId, EventStatus newStatus, Event event) {
        if (!isValidStatusChange(event.getStatus(), newStatus)) {
            log.error("Invalid status transition from {} to {} for event with ID {}", event.getStatus(), newStatus, eventId);
            throw new IllegalArgumentException("Invalid status transition");
        }

        if (newStatus == EventStatus.ACTIVE && event.getStartTime().isBefore(LocalDateTime.now())) {
            log.error("Attempt to activate event with ID {} whose start_time is in the past", eventId);
            throw new IllegalArgumentException("Cannot activate an event if start_time is in the past");
        }
    }

    private boolean isValidStatusChange(EventStatus currentStatus, EventStatus newStatus) {
        return switch (currentStatus) {
            case INACTIVE -> newStatus == EventStatus.ACTIVE;
            case ACTIVE -> newStatus == EventStatus.FINISHED;
            case FINISHED -> false;
        };
    }

    private Specification<Event> getEventSpecification(EventStatus status, SportType sportType) {
        return Specification
            .where(filterByStatus(status))
            .and(filterBySportType(sportType));
    }
}
