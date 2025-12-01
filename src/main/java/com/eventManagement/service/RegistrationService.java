package com.eventManagement.service;

import java.util.List;

import com.eventManagement.entity.Registration;

public interface RegistrationService {
    Registration registerUser(Long userId, Long eventId);

    List<Registration> getAllRegistrations();

    Registration getRegistration(Long id);

    void deleteRegistration(Long id);

    List<Registration> getRegistrationsByUser(Long userId);

    List<Registration> getAllRegistrationByEvent(Long eventId);

    Long getUserRegistrationCount(Long userId);

}