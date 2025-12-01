package com.eventManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventManagement.entity.Event;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;


public interface EventRepository extends JpaRepository<Event,Long>{

    List<Event> findByStartAtAfter(LocalDateTime startAt);
    
    List<Event> findByStartAtBefore(LocalDateTime startAt);

    Long countByStartAtAfter(LocalDateTime date);
}
