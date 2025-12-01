package com.eventManagement.service.impl;

import com.eventManagement.entity.Event;
import com.eventManagement.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private LocalDateTime futureDateTime;
    private LocalDateTime pastDateTime;

    @BeforeEach
    void setUp() {
        futureDateTime = LocalDateTime.now().plusDays(5);
        pastDateTime = LocalDateTime.now().minusDays(5);
        
        event = new Event();
        event.setId(1L);
        event.setTitle("Tech Conference");
        event.setDescription("A great tech event");
        event.setLocation("New York");
        event.setStartAt(futureDateTime);
        event.setEndAt(futureDateTime.plusHours(2));
        event.setCapacity(100);
        event.setPublished(true);
        event.setCategory("Technology");
        event.setSpeakers(new HashSet<>());
    }

    @Test
    void testCreateEvent_Success() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event createdEvent = eventService.createEvent(event);

        assertNotNull(createdEvent);
        assertEquals("Tech Conference", createdEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testCreateEvent_WithPastStartDate_ThrowsException() {
        event.setStartAt(pastDateTime);
        event.setEndAt(pastDateTime.plusHours(2));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.createEvent(event);
        });

        assertEquals("Start date should not be a past date", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testCreateEvent_WithInvalidDateRange_ThrowsException() {
        event.setStartAt(futureDateTime);
        event.setEndAt(futureDateTime.minusHours(1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.createEvent(event);
        });

        assertEquals("Start Date and time should not be after End Date and time", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_Success() {
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Conference");
        updatedEvent.setDescription("Updated description");
        updatedEvent.setLocation("Boston");
        updatedEvent.setStartAt(futureDateTime);
        updatedEvent.setEndAt(futureDateTime.plusHours(3));
        updatedEvent.setCapacity(150);
        updatedEvent.setPublished(true);
        updatedEvent.setCategory("Technology");
        updatedEvent.setSpeakers(new HashSet<>());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.updateEvent(1L, updatedEvent);

        assertNotNull(result);
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void testUpdateEvent_EventNotFound_ThrowsException() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.updateEvent(999L, event);
        });

        assertEquals("Event not found with id 999", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void testDeleteEvent_Success() {
        eventService.deleteEvent(1L);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event foundEvent = eventService.getEvent(1L);

        assertNotNull(foundEvent);
        assertEquals(1L, foundEvent.getId());
        assertEquals("Tech Conference", foundEvent.getTitle());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEvent_NotFound_ThrowsException() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEvent(999L);
        });

        assertEquals("Event not found with id 999", exception.getMessage());
    }

    @Test
    void testGetAllEvents_Success() {
        List<Event> eventList = new ArrayList<>();
        eventList.add(event);

        when(eventRepository.findAll()).thenReturn(eventList);

        List<Event> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetRecentEvents_Success() {
        List<Event> recentEvents = new ArrayList<>();
        recentEvents.add(event);

        when(eventRepository.findByStartAtAfter(any(LocalDateTime.class))).thenReturn(recentEvents);

        List<Event> result = eventService.getRecentEvents(LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByStartAtAfter(any(LocalDateTime.class));
    }

    @Test
    void testGetPastEvents_Success() {
        List<Event> pastEvents = new ArrayList<>();
        Event pastEvent = new Event();
        pastEvent.setId(2L);
        pastEvent.setTitle("Past Event");
        pastEvent.setStartAt(pastDateTime);
        pastEvent.setEndAt(pastDateTime.plusHours(2));
        pastEvents.add(pastEvent);

        when(eventRepository.findByStartAtBefore(any(LocalDateTime.class))).thenReturn(pastEvents);

        List<Event> result = eventService.getPastEvents(LocalDateTime.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByStartAtBefore(any(LocalDateTime.class));
    }

    @Test
    void testGetUpcomingEventCount_Success() {
        when(eventRepository.countByStartAtAfter(any(LocalDateTime.class))).thenReturn(5L);

        Long count = eventService.getUpcomingEventCount();

        assertEquals(5L, count);
        verify(eventRepository, times(1)).countByStartAtAfter(any(LocalDateTime.class));
    }
}
