package com.arthurdream.sporteventapi.service;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import java.util.List;
import java.util.Optional;

public interface EventService {

    EventDTO createEvent(EventDTO eventDTO);

    List<EventDTO> getEvents(EventStatus status, SportType sportType);

    Optional<EventDTO> getEventById(Long eventId);

    boolean changeEventStatus(Long eventId, EventStatus newStatus);
}
