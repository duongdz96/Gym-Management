package com.example.gympool.controller;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Attendance;
import com.example.gympool.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;
    @GetMapping("/{memberId}")
    public List<Attendance> findAll(@PathVariable Long memberId) {
        return attendanceService.getAllAttendance(memberId);
    }
    @GetMapping("/{memberId}/time")
    public List<Attendance> findByTime(@PathVariable Long memberId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return attendanceService.getAttendanceByTime(memberId,from, to);
    }
}
