package com.eventManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventManagement.entity.Event;
import com.eventManagement.entity.Registration;
import com.eventManagement.entity.User;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByUserAndEvent(User user, Event event);

    Optional<Registration> findByUserAndEvent(User user, Event event);

    List<Registration> findByEvent(Event event);

    List<Registration> findByUserId(Long userId);

    Long countByUserId(Long userId);

}
