package com.eventManagement.controller;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;
import com.eventManagement.service.EventService;
import com.eventManagement.service.RegistrationService;
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
public class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private RegistrationController registrationController;

    private User testUser;
    private Event testEvent;
    private Registration testRegistration;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole("USER");

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
    void testShowRegistrations_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(registrationService.getAllRegistrations()).thenReturn(registrations);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = registrationController.showRegistrations(model);

        assertEquals("registration-list", viewName);
        verify(registrationService, times(1)).getAllRegistrations();
        verify(model, times(1)).addAttribute("registrations", registrations);
    }

    @Test
    void testShowAddRegistrationPage_AdminUser_Success() {
        testUser.setRole("ADMIN");

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(eventService.getEvent(1L)).thenReturn(testEvent);
        when(userService.getAllUsers()).thenReturn(new ArrayList<>());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = registrationController.showAddRegistrationPage(model, 1L, principal);

        assertEquals("add-registration", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(eventService, times(1)).getEvent(1L);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testShowAddRegistrationPage_RegularUser_Success() {
        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(eventService.getEvent(1L)).thenReturn(testEvent);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = registrationController.showAddRegistrationPage(model, 1L, principal);

        assertEquals("add-registration", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(eventService, times(1)).getEvent(1L);
        verify(userService, never()).getAllUsers();
    }

    @Test
    void testRegister_UserRole_Success() {
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(registrationService).registerUser(1L, 1L);

        String viewName = registrationController.register(1L, 1L, principal);

        assertEquals("redirect:/api/events/view", viewName);
        verify(registrationService, times(1)).registerUser(1L, 1L);
    }

    @Test
    void testRegister_AdminRole_Success() {
        testUser.setRole("ADMIN");

        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(registrationService).registerUser(1L, 1L);

        String viewName = registrationController.register(1L, 1L, principal);

        assertEquals("redirect:/api/registrations/view", viewName);
        verify(registrationService, times(1)).registerUser(1L, 1L);
    }

    @Test
    void testShowRegistrationsByEvent_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(eventService.getEvent(1L)).thenReturn(testEvent);
        when(registrationService.getAllRegistrationByEvent(1L)).thenReturn(registrations);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = registrationController.showRegistrations(model, 1L);

        assertEquals("event-attendance", viewName);
        verify(eventService, times(1)).getEvent(1L);
        verify(registrationService, times(1)).getAllRegistrationByEvent(1L);
    }

    @Test
    void testDeleteRegistration_Success() {
        doNothing().when(registrationService).deleteRegistration(1L);

        String viewName = registrationController.delete(1L);

        assertEquals("redirect:/api/registrations/view", viewName);
        verify(registrationService, times(1)).deleteRegistration(1L);
    }

    @Test
    void testGetByUser_Success() {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(testRegistration);

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(registrationService.getRegistrationsByUser(1L)).thenReturn(registrations);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = registrationController.getByUser(model, principal);

        assertEquals("user-registrations", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(registrationService, times(1)).getRegistrationsByUser(1L);
    }
}
