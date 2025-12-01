package com.eventManagement.service;

import com.eventManagement.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event createEvent(Event event);

    Event updateEvent(Long id, Event event);

    void deleteEvent(Long id);

    Event getEvent(Long id);

    List<Event> getAllEvents();

    List<Event> getRecentEvents(LocalDateTime dateTime);

    List<Event> getPastEvents(LocalDateTime dateTime);

    public Long getUpcomingEventCount();
}
