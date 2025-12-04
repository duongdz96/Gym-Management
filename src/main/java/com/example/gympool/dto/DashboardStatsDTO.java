package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Long totalMembers;
    private Long activeMembers;
    private Long newMembersThisMonth;
    private Long totalClasses;
    private Long totalProducts;
    private Long pendingBills;
    private Long completedBills;
    private Double revenueGrowthRate; // Tỷ lệ tăng trưởng so với tháng trước (%)
}
