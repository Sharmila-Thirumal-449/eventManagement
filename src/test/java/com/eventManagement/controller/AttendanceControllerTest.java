package com.eventManagement.controller;

import com.eventManagement.entity.Attendance;
import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.service.AttendanceService;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private EventService eventService;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private AttendanceController attendanceController;

    private User testUser;
    private Event testEvent;
    private Registration testRegistration;
    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Tech Conference");
        testEvent.setStartAt(LocalDateTime.now().minusHours(1));
        testEvent.setEndAt(LocalDateTime.now().plusHours(1));
        testEvent.setCapacity(50);
        testEvent.setRegistrations(new HashSet<>());

        testRegistration = new Registration();
        testRegistration.setId(1L);
        testRegistration.setUser(testUser);
        testRegistration.setEvent(testEvent);
        testRegistration.setRegisteredAt(LocalDateTime.now());
        testRegistration.setConfirmed(true);

        testAttendance = new Attendance();
        testAttendance.setId(1L);
        testAttendance.setUser(testUser);
        testAttendance.setEvent(testEvent);
        testAttendance.setAttendedAt(LocalDateTime.now());
        testAttendance.setRegistration(testRegistration);
    }

    @Test
    void testMarkAttendance_Success() {
        when(registrationService.getRegistration(1L)).thenReturn(testRegistration);
        doNothing().when(attendanceService).markAttendance(1L);

        String viewName = attendanceController.markAttendance(1L);

        assertEquals("redirect:/api/registrations/1", viewName);
        verify(registrationService, times(1)).getRegistration(1L);
        verify(attendanceService, times(1)).markAttendance(1L);
    }

    @Test
    void testGetAttendanceByEvent_Success() {
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(testAttendance);

        when(attendanceService.getAttendanceByEvent(1L)).thenReturn(attendances);

        ResponseEntity<?> response = attendanceController.getAttendanceByEvent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attendances, response.getBody());
        verify(attendanceService, times(1)).getAttendanceByEvent(1L);
    }

    @Test
    void testGetAttendanceByEvent_EmptyList() {
        when(attendanceService.getAttendanceByEvent(1L)).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = attendanceController.getAttendanceByEvent(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(attendanceService, times(1)).getAttendanceByEvent(1L);
    }

    @Test
    void testGetAttendanceByUser_Success() {
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(testAttendance);

        when(attendanceService.getAttendanceByUser(1L)).thenReturn(attendances);

        ResponseEntity<?> response = attendanceController.getAttendanceByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attendances, response.getBody());
        verify(attendanceService, times(1)).getAttendanceByUser(1L);
    }

    @Test
    void testGetAttendanceByUser_EmptyList() {
        when(attendanceService.getAttendanceByUser(1L)).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = attendanceController.getAttendanceByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(attendanceService, times(1)).getAttendanceByUser(1L);
    }
}
