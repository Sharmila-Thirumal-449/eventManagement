package com.eventManagement.controller;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AuthController authController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("encoded_password");
        testUser.setEnabled(true);
        testUser.setRole("USER");
    }

    @Test
    void testShowLoginPage_Success() {
        String viewName = authController.showLoginPage();

        assertEquals("login", viewName);
    }

    @Test
    void testShowAddPage_Success() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = authController.showAddPage(model, principal);

        assertEquals("index", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any());
    }

    @Test
    void testShowDashboard_AdminUser_Success() {
        testUser.setRole("ADMIN");

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(eventService.getRecentEvents(any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = authController.showDashboard(model, principal);

        assertEquals("admin-dashboard", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(eventService, times(1)).getRecentEvents(any(LocalDateTime.class));
    }

    @Test
    void testShowDashboard_RegularUser_Success() {
        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(eventService.getRecentEvents(any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(eventService.getUpcomingEventCount()).thenReturn(5L);
        when(registrationService.getUserRegistrationCount(1L)).thenReturn(2L);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = authController.showDashboard(model, principal);

        assertEquals("user-dashboard", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(eventService, times(1)).getRecentEvents(any(LocalDateTime.class));
        verify(eventService, times(1)).getUpcomingEventCount();
        verify(registrationService, times(1)).getUserRegistrationCount(1L);
    }

    @Test
    void testRegister_WithPrincipal_AdminUser_Success() {
        testUser.setRole("ADMIN");

        when(principal.getName()).thenReturn("john@example.com");
        doNothing().when(userService).registerUser(any(User.class));

        String viewName = authController.register(testUser, principal);

        assertEquals("redirect:/api/user/view", viewName);
        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void testRegister_WithoutPrincipal_Success() {
        doNothing().when(userService).registerUser(any(User.class));

        String viewName = authController.register(testUser, null);

        assertEquals("redirect:/login?success", viewName);
        verify(userService, times(1)).registerUser(any(User.class));
    }
}
