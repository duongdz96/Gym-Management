package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionTodayStatsDTO {
    private Long newMemberships;      // Số thành viên mới hôm nay
    private Long totalCheckIns;       // Tổng số check-in hôm nay
    private Double totalRevenue;      // Tổng doanh thu hôm nay
}
