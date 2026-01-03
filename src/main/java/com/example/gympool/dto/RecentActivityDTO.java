package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentActivityDTO {
    private String type; // member, bill, class, product
    private String action; // Mô tả hoạt động
    private String time; // Thời gian tương đối (VD: "5 phút trước")
}
