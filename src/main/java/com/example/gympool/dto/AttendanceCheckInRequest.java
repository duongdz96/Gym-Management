package com.example.gympool.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Sử dụng Lombok để tự động tạo getter, setter, constructor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckInRequest {
    private Long classScheduleId;
    private Long memberId;
    private Long checkedInById;
}