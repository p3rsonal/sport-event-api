package com.arthurdream.sporteventapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import com.arthurdream.sporteventapi.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldCreateEventWithAdminRole() throws Exception {
        EventDTO eventDTO = createMockEvent("Test", SportType.TENNIS, EventStatus.ACTIVE);
        Mockito.when(eventService.createEvent(eventDTO)).thenReturn(eventDTO);

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isCreated())
               .andExpect(content().string(objectMapper.writeValueAsString(eventDTO)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldGetEventsWithUserRole() throws Exception {
        EventDTO eventDTO = createMockEvent("Test", SportType.TENNIS, EventStatus.ACTIVE);
        Mockito.when(eventService.getEvents(any(), any())).thenReturn(Collections.singletonList(eventDTO));

        mockMvc.perform(get("/api/events"))
               .andExpect(status().isOk())
               .andExpect(content().string(objectMapper.writeValueAsString(Collections.singletonList(eventDTO))));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldGetEventByIdWithUserRole() throws Exception {
        EventDTO eventDTO = createMockEvent("Test", SportType.TENNIS, EventStatus.ACTIVE);
        Mockito.when(eventService.getEventById(1L)).thenReturn(Optional.of(eventDTO));

        mockMvc.perform(get("/api/events/1"))
               .andExpect(status().isOk())
               .andExpect(content().string(objectMapper.writeValueAsString(eventDTO)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldChangeEventStatusWithAdminRole() throws Exception {
        Mockito.when(eventService.changeEventStatus(1L, EventStatus.FINISHED)).thenReturn(true);

        mockMvc.perform(patch("/api/events/1/status")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(EventStatus.FINISHED)))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundForNonExistentEventWithAdminRole() throws Exception {
        Mockito.when(eventService.getEventById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/events/1"))
               .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldReturnNotFoundWhenChangingStatusForNonExistentEventWithAdminRole() throws Exception {
        Mockito.when(eventService.changeEventStatus(1L, EventStatus.FINISHED)).thenReturn(false);

        mockMvc.perform(patch("/api/events/1/status")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(EventStatus.FINISHED)))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotCreateEventWithUserRole() throws Exception {
        EventDTO eventDTO = createMockEvent("Test", SportType.BASKETBALL, EventStatus.FINISHED);

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotChangeEventStatusWithUserRole() throws Exception {
        mockMvc.perform(patch("/api/events/1/status")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(EventStatus.FINISHED)))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldRequireAdminAuthenticationToCreateEvent() throws Exception {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setName("Test Event");
        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isForbidden());
    }

    @Test
    public void shouldRequireAuthenticationToGetEvents() throws Exception {
        mockMvc.perform(get("/api/events"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldNotCreateEventWithEmptyName() throws Exception {
        EventDTO eventDTO = createMockEvent("", SportType.FOOTBALL, EventStatus.INACTIVE);

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldNotCreateEventWithId() throws Exception {
        EventDTO eventDTO = createMockEvent("Test Event", SportType.FOOTBALL, EventStatus.INACTIVE);
        eventDTO.setId(10L);

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldNotCreateEventWithLongName() throws Exception {
        EventDTO eventDTO = createMockEvent("a".repeat(256), SportType.FOOTBALL, EventStatus.INACTIVE);

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(eventDTO)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldNotCreateEventWithInvalidStartTimeFormat() throws Exception {
        String reqWithInvalidTimeFormat = "{\"name\":\"Test Event\",\"sport\":\"FOOTBALL\",\"status\":\"INACTIVE\",\"startTime\":\"invalid_format\"}";

        mockMvc.perform(post("/api/events")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(reqWithInvalidTimeFormat))
               .andExpect(status().isBadRequest());
    }

    private EventDTO createMockEvent(String name, SportType sportType, EventStatus status) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setName(name);
        eventDTO.setSport(sportType);
        eventDTO.setStatus(status);
        eventDTO.setStartTime(LocalDateTime.now());
        return eventDTO;
    }
}
