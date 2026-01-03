package com.example.gympool.service;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<Attendance> getAllAttendance(Long memberId);
    List<Attendance> getAttendanceByTime(Long memberId, LocalDate from, LocalDate to);
}
