package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingClassDTO {
    private String className;
    private String teacher;
    private String startDate;         // Ngày bắt đầu (dd/MM/yyyy)
    private Long daysLeft;            // Số ngày còn lại đến khi bắt đầu
}
