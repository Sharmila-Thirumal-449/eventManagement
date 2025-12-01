package com.eventManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventManagement.entity.Attendance;
import com.eventManagement.entity.Registration;
import com.eventManagement.service.AttendanceService;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;

@Controller
@RequestMapping("/api/attendance")
@CrossOrigin("*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RegistrationService registrationService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/mark/{regId}")
    public String markAttendance(@PathVariable Long regId) {
        Registration registration = registrationService.getRegistration(regId);
        attendanceService.markAttendance(regId);
        return "redirect:/api/registrations/"+registration.getEvent().getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getAttendanceByEvent(@PathVariable Long eventId) {
        List<Attendance> list = attendanceService.getAttendanceByEvent(eventId);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAttendanceByUser(@PathVariable Long userId) {
        List<Attendance> list = attendanceService.getAttendanceByUser(userId);
        return ResponseEntity.ok(list);
    }
}