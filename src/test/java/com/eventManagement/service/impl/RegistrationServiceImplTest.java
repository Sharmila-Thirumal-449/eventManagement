package com.eventManagement.service.impl;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.repository.EventRepository;
import com.eventManagement.repository.RegistrationRepository;
import com.eventManagement.repository.UserRepository;
import com.eventManagement.service.EmailService;
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
public class RegistrationServiceImplTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    private User testUser;
    private Event testEvent;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Tech Conference");
        testEvent.setStartAt(LocalDateTime.now().plusDays(5));
        testEvent.setEndAt(LocalDateTime.now().plusDays(5).plusHours(2));
        testEvent.setCapacity(50);
        testEvent.setRegistrations(new HashSet<>());

        testRegistration = new Registration();
        testRegistration.setId(1L);
        testRegistration.setUser(testUser);
        testRegistration.setEvent(testEvent);
        testRegistration.setRegisteredAt(LocalDateTime.now());
        testRegistration.setConfirmed(true);
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);
        when(registrationRepository.save(any(Registration.class))).thenReturn(testRegistration);

        Registration result = registrationService.registerUser(1L, 1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getUser().getId());
        assertEquals(testEvent.getId(), result.getEvent().getId());
        verify(registrationRepository, times(1)).save(any(Registration.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(999L, 1L);
        });

        assertEquals("User Not Found", exception.getMessage());
    }

    @Test
    void testRegisterUser_EventNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(1L, 999L);
        });

        assertEquals("Event Not Found", exception.getMessage());
    }

    @Test
    void testRegisterUser_EventCapacityZero_ThrowsException() {
        testEvent.setCapacity(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(1L, 1L);
        });

        assertEquals("No seat available for this event,Registration Closed!", exception.getMessage());
    }

    @Test
    void testRegisterUser_PastEvent_ThrowsException() {
        testEvent.setStartAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(1L, 1L);
        });

        assertEquals("User cannot register for past event", exception.getMessage());
    }

    @Test
    void testRegisterUser_AlreadyRegistered_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.registerUser(1L, 1L);
        });

        assertEquals("User already registered for this event", exception.getMessage());
    }

    @Test
    void testGetAllRegistrations_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(registrationRepository.findAll()).thenReturn(registrations);

        List<Registration> result = registrationService.getAllRegistrations();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(registrationRepository, times(1)).findAll();
    }

    @Test
    void testGetRegistration_Success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));

        Registration result = registrationService.getRegistration(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(registrationRepository, times(1)).findById(1L);
    }

    @Test
    void testGetRegistration_NotFound_ThrowsException() {
        when(registrationRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.getRegistration(999L);
        });

        assertEquals("Registration Not Found", exception.getMessage());
    }

    @Test
    void testDeleteRegistration_Success() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(testRegistration));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        registrationService.deleteRegistration(1L);

        verify(registrationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteRegistration_NotFound_ThrowsException() {
        when(registrationRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.deleteRegistration(999L);
        });

        assertEquals("Registration not found", exception.getMessage());
    }

    @Test
    void testGetRegistrationsByUser_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(registrationRepository.findByUserId(1L)).thenReturn(registrations);

        List<Registration> result = registrationService.getRegistrationsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(registrationRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetAllRegistrationByEvent_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.findByEvent(testEvent)).thenReturn(registrations);

        List<Registration> result = registrationService.getAllRegistrationByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(registrationRepository, times(1)).findByEvent(testEvent);
    }

    @Test
    void testGetUserRegistrationCount_Success() {
        when(registrationRepository.countByUserId(1L)).thenReturn(3L);

        Long count = registrationService.getUserRegistrationCount(1L);

        assertEquals(3L, count);
        verify(registrationRepository, times(1)).countByUserId(1L);
    }
}
