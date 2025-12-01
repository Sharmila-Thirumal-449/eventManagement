package com.eventManagement.service.impl;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.RegistrationRepository;
import com.eventManagement.service.EventService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public Event createEvent(Event event) {
        if(event.getStartAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Start date should not be a past date");
        }
        if(event.getStartAt().isAfter(event.getEndAt())){
            throw new RuntimeException("Start Date and time should not be after End Date and time");
        }
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
        if(event.getStartAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Start date should not be a past date");
        }
                        
        if(updatedEvent.getStartAt().isAfter(updatedEvent.getEndAt())){
            throw new RuntimeException("Start Date and time should not be after End Date and time");
        }
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setLocation(updatedEvent.getLocation());
        event.setStartAt(updatedEvent.getStartAt());
        event.setEndAt(updatedEvent.getEndAt());
        event.setCapacity(updatedEvent.getCapacity());
        event.setPublished(updatedEvent.isPublished());
        event.setCategory(updatedEvent.getCategory());
        event.setSpeakers(updatedEvent.getSpeakers());

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(()->
    new RuntimeException("Event not found"));
    if(event.getStartAt().isBefore(LocalDateTime.now())){
        throw new RuntimeException("Cannot delete past event");
    }
        List<Registration> registrations =registrationRepository.findByEvent(event);
        if(registrations.size()!=0){
            throw new RuntimeException("Cannot delete this event because user registered for this event!");
        }
        eventRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getRecentEvents(LocalDateTime dateTime) {
        return eventRepository.findByStartAtAfter(dateTime);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUpcomingEventCount() {
        return eventRepository.countByStartAtAfter(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getPastEvents(LocalDateTime dateTime) {
        return eventRepository.findByStartAtBefore(LocalDateTime.now());
    }
}