package com.arthurdream.sporteventapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.arthurdream.sporteventapi.dto.EventDTO;
import com.arthurdream.sporteventapi.model.EventStatus;
import com.arthurdream.sporteventapi.model.SportType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Sql(scripts={"classpath:data/data.sql"},
    config=@SqlConfig(transactionMode = TransactionMode.ISOLATED),
    executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class SportEventIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateEvent() throws Exception {
        String eventJson = "{\"name\":\"Test Event\",\"sport\":\"FOOTBALL\",\"status\":\"INACTIVE\",\"startTime\":\"2023-09-07T14:30:00\"}";

        MvcResult result = mockMvc.perform(post("/api/events")
                                      .contentType("application/json")
                                      .content(eventJson))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        EventDTO createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getName());
        assertEquals(SportType.FOOTBALL, createdEvent.getSport());
        assertEquals(EventStatus.INACTIVE, createdEvent.getStatus());
        assertEquals(LocalDateTime.parse("2023-09-07T14:30:00"), createdEvent.getStartTime());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetEventsByStatus() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/events")
                                      .param("status", "INACTIVE"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(event -> event.getStatus().equals(EventStatus.INACTIVE)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetEventsBySportType() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/events")
                                      .param("sportType", "FOOTBALL"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<EventDTO>>() {});
        assertNotNull(events);
        assertFalse(events.isEmpty());
        assertTrue(events.stream().allMatch(event -> event.getSport().equals(SportType.FOOTBALL)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetEventById() throws Exception {
        Long eventId = createEventAndGetId(EventStatus.ACTIVE);

        MvcResult result = mockMvc.perform(get("/api/events/" + eventId))
                                  .andExpect(status().isOk())
                                  .andReturn();

        EventDTO event = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(event);
        assertEquals("Test Event", event.getName());
        assertEquals(SportType.FOOTBALL, event.getSport());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertEquals(LocalDate.now().plusDays(1), event.getStartTime().toLocalDate());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testChangeEventStatus() throws Exception {
        Long eventId = createEventAndGetId(EventStatus.INACTIVE);

        mockMvc.perform(patch("/api/events/" + eventId + "/status")
                   .contentType("application/json")
                   .content("\"ACTIVE\""))
               .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/api/events/" + eventId))
                                  .andExpect(status().isOk())
                                  .andReturn();

        EventDTO event = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertEquals(EventStatus.ACTIVE, event.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testEventNotFound() throws Exception {
        mockMvc.perform(get("/api/events/0"))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateEventWithEmptyStatus() throws Exception {
        String eventJson = "{\"name\":\"Test Event 2\",\"sport\":\"HOCKEY\",\"startTime\":\"2023-09-07T14:30:00\"}";

        MvcResult result = mockMvc.perform(post("/api/events")
                                      .contentType("application/json")
                                      .content(eventJson))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        EventDTO createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(createdEvent);
        assertEquals("Test Event 2", createdEvent.getName());
        assertEquals(SportType.HOCKEY, createdEvent.getSport());
        assertNull(createdEvent.getStatus());
        assertEquals(LocalDateTime.parse("2023-09-07T14:30:00"), createdEvent.getStartTime());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateEventWithEmptySportType() throws Exception {
        String eventJson = "{\"name\":\"Test Event 3\",\"status\":\"ACTIVE\",\"startTime\":\"2023-09-07T15:30:00\"}";

        MvcResult result = mockMvc.perform(post("/api/events")
                                      .contentType("application/json")
                                      .content(eventJson))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        EventDTO createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(createdEvent);
        assertEquals("Test Event 3", createdEvent.getName());
        assertNull(createdEvent.getSport());
        assertEquals(EventStatus.ACTIVE, createdEvent.getStatus());
        assertEquals(LocalDateTime.parse("2023-09-07T15:30:00"), createdEvent.getStartTime());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateEventWithInvalidStatusChange() throws Exception {
        Long eventId = createEventAndGetId(EventStatus.FINISHED);

        mockMvc.perform(patch("/api/events/" + eventId + "/status")
                   .contentType("application/json")
                   .content("\"ACTIVE\""))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetEventsWithoutAnyFilters() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/events"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
        assertNotNull(events);
        assertEquals(13, events.size());
    }

    private Long createEventAndGetId(EventStatus status) throws Exception {
        String eventJson = "{\"name\":\"Test Event\",\"sport\":\"FOOTBALL\",\"status\":\"" + status.toString() + "\",\"startTime\":\"" + LocalDateTime.now().plusDays(1) + "\"}";

        MvcResult result = mockMvc.perform(post("/api/events")
                                      .contentType("application/json")
                                      .content(eventJson))
                                  .andExpect(status().isCreated())
                                  .andReturn();

        EventDTO createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        return createdEvent.getId();
    }
}
