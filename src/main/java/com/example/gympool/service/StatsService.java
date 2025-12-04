package com.example.gympool.service;

import com.example.gympool.dto.DashboardStatsDTO;
import com.example.gympool.dto.MembershipDistributionDTO;
import com.example.gympool.dto.MonthlyRevenueDTO;
import com.example.gympool.dto.ProductStatDTO;
import com.example.gympool.dto.RecentActivityDTO;

import java.util.List;

public interface StatsService {
    List<ProductStatDTO> getTopSellingProducts();
    DashboardStatsDTO getDashboardStats();
    List<MonthlyRevenueDTO> getMonthlyRevenue(int year);
    List<MembershipDistributionDTO> getMembershipDistribution();
    List<RecentActivityDTO> getRecentActivities();
}
