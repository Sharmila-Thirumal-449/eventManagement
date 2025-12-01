package com.eventManagement.service.impl;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.RegistrationRepository;
import com.eventManagement.repository.UserRepository;
import com.eventManagement.service.EmailService;
import com.eventManagement.service.RegistrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EmailService emailService;
    /*
     * @Override
     * public Registration registerUser(Long userId, Long eventId) {
     * User user = userRepository.findById(userId)
     * .orElseThrow(() -> new RuntimeException("User Not Found"));
     * Event event = eventRepository.findById(eventId)
     * .orElseThrow(() -> new RuntimeException("Event Not Found"));
     * if (registrationRepository.existsByUserAndEvent(user, event)) {
     * throw new RuntimeException("User already registered for this event");
     * }
     * Registration registration = Registration.builder()
     * .user(user)
     * .event(event)
     * .registeredAt(LocalDateTime.now())
     * .confirmed(true)
     * .build();
     * return registrationRepository.save(registration);
     * }
     */

    @Override
    public Registration registerUser(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event Not Found"));
        if(event.getCapacity()<=0){
            throw new RuntimeException("No seat available for this event,Registration Closed!");
        }
        if (registrationRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("User already registered for this event");
        }
        if(event.getStartAt().isBefore((LocalDateTime.now()))){
            throw new RuntimeException("User cannot register for past event");
        }
        Registration registration = Registration.builder()
                .user(user)
                .event(event)
                .registeredAt(LocalDateTime.now())
                .confirmed(true)
                .build();

        Registration saved = registrationRepository.save(registration);

        String subject = "Event Registration Confirmation";
        String message = "Hello " + user.getName() + ",\n\n"
                + "You have successfully registered for the event:\n"
                + event.getTitle() + "\n"
                + "StartDate: " + event.getStartAt() + "\n"
                + "EndDate: " + event.getEndAt() + "\n"
                + "Venue: " + event.getLocation() + "\n\n"
                + "Thank you for registering.\n\n"
                + "Regards,\nEvent Management Team";

        emailService.sendEmail(user.getEmail(), subject, message);
        event.setCapacity(event.getCapacity()-1);
        eventRepository.save(event);
        return saved;
    }

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public Registration getRegistration(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration Not Found"));
    }

    @Override
    public void deleteRegistration(Long id) {
        Registration registration = registrationRepository.findById(id).orElseThrow(
            ()->new RuntimeException("Registration not found")
        );
        Event event =eventRepository.findById(registration.getEvent().getId()).orElseThrow(
            ()->new RuntimeException("Event not found")
        );
        User user = userRepository.findById(registration.getUser().getId()).orElseThrow(()->
        new RuntimeException("User not found"));
        event.setCapacity(event.getCapacity()+1);
        registrationRepository.deleteById(id);
    }

    @Override
    public List<Registration> getRegistrationsByUser(Long userId) {
       return registrationRepository.findByUserId(userId);
    }

    @Override
    public List<Registration> getAllRegistrationByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
        return registrationRepository.findByEvent(event);
    }

    @Override
    public Long getUserRegistrationCount(Long userId) {
        return registrationRepository.countByUserId(userId);
    }

}
