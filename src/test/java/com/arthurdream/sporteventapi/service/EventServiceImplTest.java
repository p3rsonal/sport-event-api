package com.arthurdream.sporteventapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.model.Event;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import com.arthurdream.sporteventapi.repository.EventRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"app.cache.enabled=false"})
public class EventServiceImplTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;


    @Test
    public void shouldCreateEvent() {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setName("Test Event");

        Event event = new Event();
        event.setId(1L);
        event.setName("Test Event");

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.createEvent(eventDTO);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    public void shouldGetEventById() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setName("Test Event");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.getEventById(eventId);

        verify(eventRepository).findById(eventId);
    }

    @Test
    public void shouldGetEventsWithStatusAndSportType() {
        List<Event> mockEvents = List.of(new Event());
        when(eventRepository.findAll(any(Specification.class))).thenReturn(mockEvents);

        List<EventDTO> result = eventService.getEvents(EventStatus.ACTIVE, SportType.FOOTBALL);

        assertEquals(1, result.size());
        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    public void shouldGetEventsWithOnlyStatus() {
        List<Event> mockEvents = List.of(new Event());
        when(eventRepository.findAll(any(Specification.class))).thenReturn(mockEvents);

        List<EventDTO> result = eventService.getEvents(EventStatus.ACTIVE, null);

        assertEquals(1, result.size());
        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    public void shouldGetEventsWithOnlySportType() {
        List<Event> mockEvents = List.of(new Event());
        when(eventRepository.findAll(any(Specification.class))).thenReturn(mockEvents);

        List<EventDTO> result = eventService.getEvents(null, SportType.FOOTBALL);

        assertEquals(1, result.size());
        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    public void shouldGetAllEventsWhenNoStatusOrSportTypeProvided() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setName("Test Event 1");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setName("Test Event 2");

        List<Event> events = Arrays.asList(event1, event2);
        when(eventRepository.findAll(any(Specification.class))).thenReturn(events);

        List<EventDTO> result = eventService.getEvents(null, null);

        assertEquals(2, result.size());
        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    public void shouldChangeEventStatus() {
        Event mockEvent = new Event();
        mockEvent.setStatus(EventStatus.INACTIVE);
        mockEvent.setStartTime(LocalDateTime.now().plusHours(1));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        boolean result = eventService.changeEventStatus(1L, EventStatus.ACTIVE);

        assertTrue(result);
        verify(eventRepository).updateStatus(any(Long.class), any(EventStatus.class));
    }

    @Test
    public void shouldReturnFalseWhenChangingStatusOfNonExistentEvent() {
        Long eventId = 1L;
        EventStatus newStatus = EventStatus.ACTIVE;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        boolean result = eventService.changeEventStatus(eventId, newStatus);

        assertFalse(result);
        verify(eventRepository).findById(eventId);
    }

    @Test
    public void shouldNotChangeEventStatusForInvalidTransition() {
        Event mockEvent = new Event();
        mockEvent.setStatus(EventStatus.FINISHED);
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.changeEventStatus(1L, EventStatus.ACTIVE));
    }

    @Test
    public void shouldThrowExceptionWhenChangingActiveEventToInactive() {
        Long eventId = 1L;

        Event event = new Event();
        event.setId(eventId);
        event.setName("Test Event");
        event.setStatus(EventStatus.ACTIVE);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () -> eventService.changeEventStatus(eventId, EventStatus.INACTIVE));
        verify(eventRepository).findById(eventId);
    }

    @Test
    public void shouldNotActivateEventWithPastStartTime() {
        Event mockEvent = new Event();
        mockEvent.setStatus(EventStatus.INACTIVE);
        mockEvent.setStartTime(LocalDateTime.now().minusHours(1));
        when(eventRepository.findById(any(Long.class))).thenReturn(Optional.of(mockEvent));

        assertThrows(IllegalArgumentException.class, () -> eventService.changeEventStatus(1L, EventStatus.ACTIVE));
    }
}
