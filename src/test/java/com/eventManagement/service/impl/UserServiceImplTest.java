package com.eventManagement.service.impl;

import com.eventManagement.entity.User;
import com.eventManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private String testPassword = "password123";
    private String encodedPassword = "encoded_password123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword(encodedPassword);
        testUser.setEnabled(true);
        testUser.setRole("USER");
    }

    @Test
    void testRegisterUser_Success() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");
        newUser.setPassword(testPassword);
        newUser.setRole("USER");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registeredUser = userService.registerUser(newUser);

        assertNotNull(registeredUser);
        assertEquals("john@example.com", registeredUser.getEmail());
        assertTrue(registeredUser.isEnabled());
        verify(userRepository, times(1)).existsByEmail("jane@example.com");
        verify(passwordEncoder, times(1)).encode(testPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists_ThrowsException() {
        User newUser = new User();
        newUser.setEmail("john@example.com");
        newUser.setPassword(testPassword);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(newUser);
        });

        assertEquals("Email already exists!", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);

        User foundUser = userService.getUserByEmail("john@example.com");

        assertNotNull(foundUser);
        assertEquals("john@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void testUpdateProfile_Success() {
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john_updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("john_updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateProfile(1L, updatedUser);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateProfile(999L, testUser);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateProfile_EmailAlreadyExists_ThrowsException() {
        User updatedUser = new User();
        updatedUser.setName("John Updated");
        updatedUser.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateProfile(1L, updatedUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound_ThrowsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsers_Success() {
        List<User> userList = new ArrayList<>();
        userList.add(testUser);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testChangePassword_Success() {
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String confirmPassword = "newPassword";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);
        when(passwordEncoder.matches(newPassword, encodedPassword)).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_newPassword");

        userService.changePassword(1L, oldPassword, newPassword, confirmPassword);

        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).save(any(User.class));
    }

    @Test
    void testChangePassword_WrongOldPassword_ThrowsException() {
        String oldPassword = "wrongPassword";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(1L, oldPassword, "newPassword", "newPassword");
        });

        assertEquals("Old password doesn't match", exception.getMessage());
    }

    @Test
    void testChangePassword_PasswordMismatch_ThrowsException() {
        String oldPassword = "oldPassword";

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.changePassword(1L, oldPassword, "newPassword", "differentPassword");
        });

        assertEquals("New password and confirm password doesn't match", exception.getMessage());
    }
}
