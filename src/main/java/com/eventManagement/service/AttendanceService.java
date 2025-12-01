package com.eventManagement.service;

import java.util.List;

import com.eventManagement.entity.Attendance;

public interface AttendanceService {

    Attendance markAttendance(Long registrationId);

    List<Attendance> getAttendanceByEvent(Long eventId);

    List<Attendance> getAttendanceByUser(Long userId);
}
