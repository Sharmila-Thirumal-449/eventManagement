package com.eventManagement.repository;

import com.eventManagement.entity.Speaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {
    boolean existsByEmail(String email);
}
