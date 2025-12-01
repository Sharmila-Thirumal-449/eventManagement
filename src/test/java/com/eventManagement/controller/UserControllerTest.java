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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

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
    private UserController userController;

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
    void testShowAllUsers_Success() {
        List<User> users = new ArrayList<>();
        users.add(testUser);

        when(userService.getAllUsers()).thenReturn(users);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = userController.showAllUsers(model);

        assertEquals("users", viewName);
        verify(userService, times(1)).getAllUsers();
        verify(model, times(1)).addAttribute("users", users);
    }

    @Test
    void testShowAddPage_Success() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = userController.showAddPage(model);

        assertEquals("index", viewName);
        verify(model, times(1)).addAttribute(eq("user"), any());
    }

    @Test
    void testShowUserProfile_Success() {
        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = userController.showUserProfile(model, principal);

        assertEquals("profile", viewName);
        verify(userService, times(1)).getUserByEmail("john@example.com");
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testShowEditPage_Success() {
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        String viewName = userController.showEditPage(1L, model);

        assertEquals("edit-profile", viewName);
        verify(userService, times(1)).getUserById(1L);
        verify(model, times(1)).addAttribute("user", testUser);
    }

    @Test
    void testChangePasswordPage_Success() {
        String viewName = userController.changePasswordPage();

        assertEquals("change-password", viewName);
    }

    @Test
    void testUpdatePassword_Success() {
        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(userService).changePassword(anyLong(), anyString(), anyString(), anyString());

        String viewName = userController.updatePassword(principal, "oldPass", "newPass", "newPass");

        assertEquals("redirect:/api/user/profile?passwordChanged=true", viewName);
        verify(userService, times(1)).changePassword(1L, "oldPass", "newPass", "newPass");
    }

    @Test
    void testUpdateUser_AdminUser_Success() {
        testUser.setRole("ADMIN");
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(userService).updateProfile(anyLong(), any(User.class));

        String viewName = userController.updateUser(1L, updatedUser, principal);

        assertEquals("redirect:/api/user/profile", viewName);
        verify(userService, times(1)).updateProfile(1L, updatedUser);
    }

    @Test
    void testUpdateUser_AdminUpdatingOtherUser_Success() {
        testUser.setRole("ADMIN");
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Jane Doe");

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(userService).updateProfile(anyLong(), any(User.class));

        String viewName = userController.updateUser(2L, otherUser, principal);

        assertEquals("redirect:/api/user/view", viewName);
        verify(userService, times(1)).updateProfile(2L, otherUser);
    }

    @Test
    void testUpdateUser_RegularUser_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");

        when(principal.getName()).thenReturn("john@example.com");
        when(userService.getUserByEmail("john@example.com")).thenReturn(testUser);
        doNothing().when(userService).updateProfile(anyLong(), any(User.class));

        String viewName = userController.updateUser(1L, updatedUser, principal);

        assertEquals("redirect:/api/user/profile", viewName);
        verify(userService, times(1)).updateProfile(1L, updatedUser);
    }

    @Test
    void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        String viewName = userController.deleteUser(1L);

        assertEquals("redirect:/api/user/view", viewName);
        verify(userService, times(1)).deleteUser(1L);
    }
}
