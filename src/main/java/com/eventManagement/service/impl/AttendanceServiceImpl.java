package com.eventManagement.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eventManagement.entity.Attendance;
import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.repository.AttendanceRepository;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.RegistrationRepository;
import com.eventManagement.repository.UserRepository;
import com.eventManagement.service.AttendanceService;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public Attendance markAttendance(Long regId) {
        Registration registration =registrationRepository.findById(regId).orElseThrow(()->new RuntimeException("Registration not found"));
        User user = userRepository.findById(registration.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(registration.getEvent().getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (attendanceRepository.existsByUserAndEvent(user, event)) {
            throw new RuntimeException("Attendance already marked");
        }

        if(event.getEndAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Event already ended");
        }
        Attendance attendance = new Attendance();
        attendance.setEvent(event);
        attendance.setUser(user);
        attendance.setAttendedAt(LocalDateTime.now());
        attendance.setRegistration(registration);
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAttendanceByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return attendanceRepository.findByEvent(event);
    }

    @Override
    public List<Attendance> getAttendanceByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return attendanceRepository.findByUser(user);
    }
}