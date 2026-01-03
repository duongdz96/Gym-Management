package com.example.gympool.service;

import com.example.gympool.dto.AttendanceCheckInRequest;
import com.example.gympool.entity.StudentClassAttendance;

import java.util.List;

public interface StudentClassAttendanceService {
    StudentClassAttendance checkInStudent(AttendanceCheckInRequest request);
    List<StudentClassAttendance> getAttendanceByClassSchedule(Long classScheduleId);
    int processAutoAbsent(Long classScheduleId);
    StudentClassAttendance updateAttendanceStatus(Long classScheduleId, Long memberId, Long teacherId, String newStatus);
}
