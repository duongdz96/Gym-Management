package com.example.gympool.controller;

import com.example.gympool.dto.*;
import com.example.gympool.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(statsService.getDashboardStats());
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<List<MonthlyRevenueDTO>> getRevenueChart(@RequestParam(required = false) Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        return ResponseEntity.ok(statsService.getMonthlyRevenue(year));
    }

    @GetMapping("/membership-distribution")
    public ResponseEntity<List<MembershipDistributionDTO>> getMembershipDistribution() {
        return ResponseEntity.ok(statsService.getMembershipDistribution());
    }

    @GetMapping("/products/ranking")
    public ResponseEntity<List<ProductStatDTO>> getProductRanking() {
        List<ProductStatDTO> ranking = statsService.getTopSellingProducts();
        return ResponseEntity.ok(ranking);
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivities() {
        return ResponseEntity.ok(statsService.getRecentActivities());
    }
}