package com.eventManagement.repository;

import com.eventManagement.entity.Attendance;
import com.eventManagement.entity.Event;
import com.eventManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByEvent(Event event);

    List<Attendance> findByUser(User user);

    boolean existsByUserAndEvent(User user, Event event);
}
