package com.eventManagement.service.impl;

import com.eventManagement.entity.Attendance;
import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.repository.AttendanceRepository;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.RegistrationRepository;
import com.eventManagement.repository.UserRepository;
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
public class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

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
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(attendanceRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(testAttendance);

        Attendance result = attendanceService.markAttendance(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getUser().getId());
        assertEquals(testEvent.getId(), result.getEvent().getId());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_RegistrationNotFound_ThrowsException() {
        when(registrationRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(999L);
        });

        assertEquals("Registration not found", exception.getMessage());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_UserNotFound_ThrowsException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(1L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_EventNotFound_ThrowsException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(1L);
        });

        assertEquals("Event not found", exception.getMessage());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_AlreadyMarked_ThrowsException() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(attendanceRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(1L);
        });

        assertEquals("Attendance already marked", exception.getMessage());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void testMarkAttendance_EventEnded_ThrowsException() {
        testEvent.setEndAt(LocalDateTime.now().minusHours(1));

        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.markAttendance(1L);
        });

        assertEquals("Event already ended", exception.getMessage());
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    void testGetAttendanceByEvent_Success() {
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(testAttendance);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(attendanceRepository.findByEvent(testEvent)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getAttendanceByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findByEvent(testEvent);
    }

    @Test
    void testGetAttendanceByEvent_EventNotFound_ThrowsException() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByEvent(999L);
        });

        assertEquals("Event not found", exception.getMessage());
    }

    @Test
    void testGetAttendanceByUser_Success() {
        List<Attendance> attendances = new ArrayList<>();
        attendances.add(testAttendance);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(attendanceRepository.findByUser(testUser)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getAttendanceByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(attendanceRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testGetAttendanceByUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attendanceService.getAttendanceByUser(999L);
        });

        assertEquals("User not found", exception.getMessage());
    }
}
