package com.eventManagement.service.impl;
import com.eventManagement.entity.User;
import com.eventManagement.service.UserService;
import com.eventManagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRole(user.getRole());
        return userRepository.save(user);
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user.equals(null)) {
            new RuntimeException("User not found");
        }
        return user;
    }

    @Override
    public User updateProfile(Long id, User updatedUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        if (!Objects.equals(updatedUser.getEmail(), user.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail()))
                throw new RuntimeException("Email already exists");
            user.setEmail(updatedUser.getEmail());
        }

        /*
         * if(!Objects.equals(updatedUser.getRole().getId(), user.getRole().getId())){
         * Role role =
         * roleRepository.findById(updatedUser.getRole().getId()).orElseThrow(()->
         * new RuntimeException("Role not found"));
         * user.setRole(role);
         * }
         */

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String currentPassword, String confirmPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password doesn't match");
        }

        if (!currentPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password doesn't match");
        }

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("New password cannot be the same as old password");
        }

        user.setPassword(passwordEncoder.encode(currentPassword));
        userRepository.save(user);
    }

}
