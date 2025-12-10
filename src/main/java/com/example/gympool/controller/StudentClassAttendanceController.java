package com.example.gympool.controller;

import com.example.gympool.service.StudentClassAttendanceService;
import com.example.gympool.dto.AttendanceCheckInRequest;
import com.example.gympool.entity.StudentClassAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance/students")
public class StudentClassAttendanceController {
    @Autowired
    private StudentClassAttendanceService attendanceService;
    /**
     * API 1: Điểm danh thủ công (Mark PRESENT)
     * Thường được gọi khi giáo viên ấn "Có mặt" cho một học viên.
     * Hoặc cập nhật từ ABSENT sang PRESENT (như đã sửa trong Service).
     * POST /api/attendance/students/checkin
     */
    @PostMapping("/checkin")
    public ResponseEntity<StudentClassAttendance> checkInStudent(
            @RequestBody AttendanceCheckInRequest request) {
        try {
            StudentClassAttendance attendance = attendanceService.checkInStudent(request);
            return new ResponseEntity<>(attendance, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    /**
     * API 2: Cập nhật trạng thái điểm danh (Chỉnh sửa/Hủy điểm danh)
     * Được sử dụng khi giáo viên sửa lỗi hoặc đánh dấu Vắng mặt sau khi đã check-in.
     * PUT /api/attendance/students/update
     */
    @PutMapping("/update")
    public ResponseEntity<StudentClassAttendance> updateAttendance(
            @RequestParam Long scheduleId,
            @RequestParam Long memberId,
            @RequestParam Long teacherId,
            @RequestParam String status) {
        try {
            StudentClassAttendance updatedAttendance = attendanceService
                    .updateAttendanceStatus(scheduleId, memberId, teacherId, status);
            return ResponseEntity.ok(updatedAttendance);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    /**
     * API 3: Lấy danh sách điểm danh cho một buổi học
     * GET /api/attendance/students/{scheduleId}
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<List<StudentClassAttendance>> getAttendanceByClass(
            @PathVariable Long scheduleId) {
        List<StudentClassAttendance> attendanceList = attendanceService.getAttendanceByClassSchedule(scheduleId);
        if (attendanceList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attendanceList);
    }

    @PostMapping("/auto-absent/{scheduleId}")
    public ResponseEntity<String> processAutoAbsent(
            @PathVariable Long scheduleId) {
        try {
            int absentCount = attendanceService.processAutoAbsent(scheduleId);

            String responseMessage = String.format("Class schedule %d finalized. %d students were automatically marked ABSENT.", scheduleId, absentCount);

            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}