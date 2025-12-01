package com.eventManagement.service;

import java.util.List;

import com.eventManagement.entity.User;

public interface UserService {
    User registerUser(User user);

    User getUserByEmail(String email);

    User updateProfile(Long id, User user);

    void deleteUser(Long id);

    User getUserById(Long id);
    
    List<User> getAllUsers();

    void changePassword(Long uesrId,String oldPassword,String currentPassword,String confirmPassword);
}