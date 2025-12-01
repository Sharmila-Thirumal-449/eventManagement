package com.eventManagement.controller;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.SpeakerService;
import com.eventManagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService eventService;

    @Mock
    private SpeakerService speakerService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private EventController eventController;

    private Event testEvent;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Tech Conference");
        testEvent.setDescription("A great tech event");
        testEvent.setLocation("New York");
        testEvent.setStartAt(LocalDateTime.now().plusDays(5));
        testEvent.setEndAt(LocalDateTime.now().plusDays(5).plusHours(2));
        testEvent.setCapacity(100);
        testEvent.setPublished(true);
        testEvent.setCategory("Technology");
        testEvent.setSpeakers(new HashSet<>());
        testEvent.setRegistrations(new HashSet<>());

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole("USER");
    }

    @Test
    void testShowAllAttendence_Success() {
        List<Event> events = new ArrayList<>();
        events.add(testEvent);

        when(principal.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(eventService.getRecentEvents(any(LocalDateTime.class))).thenReturn(events);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.showAllAttendence(model, principal);

        assertEquals("events", viewName);
        verify(eventService, times(1)).getRecentEvents(any(LocalDateTime.class));
        verify(userService, times(1)).getUserByEmail("test@example.com");
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }

    @Test
    void testShowAddEventPage_Success() {
        when(speakerService.getAllSpeakers()).thenReturn(new ArrayList<>());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.showAddEventPage(model);

        assertEquals("event-form", viewName);
        verify(speakerService, times(1)).getAllSpeakers();
    }

    @Test
    void testShowEventPage_Success() {
        when(eventService.getEvent(1L)).thenReturn(testEvent);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.showEventPage(model, 1L);

        assertEquals("event-details", viewName);
        verify(eventService, times(1)).getEvent(1L);
    }

    @Test
    void testGetRecentEvents_Success() {
        List<Event> events = new ArrayList<>();
        events.add(testEvent);

        when(principal.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(eventService.getRecentEvents(any(LocalDateTime.class))).thenReturn(events);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.getRecentEvents(model, principal);

        assertEquals("events", viewName);
        verify(eventService, times(1)).getRecentEvents(any(LocalDateTime.class));
    }

    @Test
    void testGetPastEvents_Success() {
        List<Event> events = new ArrayList<>();
        Event pastEvent = new Event();
        pastEvent.setId(2L);
        pastEvent.setTitle("Past Event");
        pastEvent.setStartAt(LocalDateTime.now().minusDays(5));
        pastEvent.setEndAt(LocalDateTime.now().minusDays(5).plusHours(2));
        events.add(pastEvent);

        when(principal.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUser);
        when(eventService.getPastEvents(any(LocalDateTime.class))).thenReturn(events);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.getPastEvents(model, principal);

        assertEquals("events", viewName);
        verify(eventService, times(1)).getPastEvents(any(LocalDateTime.class));
    }

    @Test
    void testCreateEvent_Success() {
        doNothing().when(eventService).createEvent(any(Event.class));

        String viewName = eventController.createEvent(testEvent);

        assertEquals("redirect:/api/events/view", viewName);
        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    void testShowEditEventPage_Success() {
        when(eventService.getEvent(1L)).thenReturn(testEvent);
        when(speakerService.getAllSpeakers()).thenReturn(new ArrayList<>());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = eventController.showEditEventPage(model, 1L);

        assertEquals("event-form", viewName);
        verify(eventService, times(1)).getEvent(1L);
        verify(speakerService, times(1)).getAllSpeakers();
    }

    @Test
    void testUpdateEvent_Success() {
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Event");
        updatedEvent.setStartAt(LocalDateTime.now().plusDays(10));
        updatedEvent.setEndAt(LocalDateTime.now().plusDays(10).plusHours(2));
        updatedEvent.setSpeakers(new HashSet<>());

        doNothing().when(eventService).updateEvent(anyLong(), any(Event.class));

        String viewName = eventController.updateEvent(1L, updatedEvent);

        assertEquals("redirect:/api/events/view", viewName);
        verify(eventService, times(1)).updateEvent(1L, updatedEvent);
    }

    @Test
    void testDeleteEvent_Success() {
        doNothing().when(eventService).deleteEvent(1L);

        String viewName = eventController.deleteEvent(1L);

        assertEquals("redirect:/dashboard", viewName);
        verify(eventService, times(1)).deleteEvent(1L);
    }
}
