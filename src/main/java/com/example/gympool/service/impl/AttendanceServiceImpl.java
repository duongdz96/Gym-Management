package com.example.gympool.service.impl;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Attendance;
import com.example.gympool.repository.AttendanceRepository;
import com.example.gympool.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    public List<Attendance> getAllAttendance(Long memberId){
        return attendanceRepository.findByUserId(memberId);
    }

    public List<Attendance> getAttendanceByTime(Long memberId, LocalDate from, LocalDate to){
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();
        return attendanceRepository.findByTime( memberId,start, end);
    }
}
