package com.example.gympool.dto;

import lombok.*;

// Sử dụng Lombok để tự động tạo getter, setter, constructor
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttendanceCheckInRequest {
    private Long classScheduleId;
    private Long memberId;
    private Long checkedInById;
}