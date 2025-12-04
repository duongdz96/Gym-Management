package com.example.gympool.service.impl;

import com.example.gympool.dto.*;
import com.example.gympool.entity.Bill;
import com.example.gympool.entity.Member;
import com.example.gympool.repository.*;
import com.example.gympool.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private SoldProductRepository soldProductRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CustomerMembershipRepository customerMembershipRepository;
    @Autowired
    private FitnessClassRepository fitnessClassRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductStatDTO> getTopSellingProducts() {
        return soldProductRepository.getProductSalesRanking();
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        // Revenue (Current Month Only)
        // Thay vì lấy tổng doanh thu toàn thời gian, chỉ lấy tháng này
        Double currentMonthRevenue = billRepository.getRevenueByMonthAndYear(currentMonth, currentYear);
        stats.setTotalRevenue(currentMonthRevenue != null ? currentMonthRevenue : 0.0);
        
        // Members
        stats.setTotalMembers(memberRepository.count());
        stats.setActiveMembers(memberRepository.countActiveMembers());
        
        stats.setNewMembersThisMonth(memberRepository.countNewMembers(currentMonth, currentYear));
        
        // Other counts
        stats.setTotalClasses(fitnessClassRepository.count());
        stats.setTotalProducts(productRepository.count());
        stats.setPendingBills(billRepository.countPendingBills());
        stats.setCompletedBills(billRepository.countCompletedBills());
        
        // Growth Rate
        int lastMonth = currentMonth == 1 ? 12 : currentMonth - 1;
        int lastMonthYear = currentMonth == 1 ? currentYear - 1 : currentYear;
        
        Double lastMonthRev = billRepository.getRevenueByMonthAndYear(lastMonth, lastMonthYear);
        
        if (currentMonthRevenue == null) currentMonthRevenue = 0.0;
        if (lastMonthRev == null) lastMonthRev = 0.0;
        
        if (lastMonthRev > 0) {
            double growth = ((currentMonthRevenue - lastMonthRev) / lastMonthRev) * 100;
            stats.setRevenueGrowthRate(Math.round(growth * 10.0) / 10.0);
        } else {
            stats.setRevenueGrowthRate(currentMonthRevenue > 0 ? 100.0 : 0.0);
        }
        
        return stats;
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        List<MonthlyRevenueDTO> data = billRepository.getMonthlyRevenue(year);
        // Ensure month format is T1, T2...
        data.forEach(d -> d.setMonth("T" + d.getMonth()));
        return data;
    }

    @Override
    public List<MembershipDistributionDTO> getMembershipDistribution() {
        List<MembershipDistributionDTO> dist = customerMembershipRepository.getMembershipDistribution();
        long total = dist.stream().mapToLong(MembershipDistributionDTO::getCount).sum();
        
        if (total > 0) {
            dist.forEach(d -> {
                double percentage = (double) d.getCount() / total * 100;
                d.setPercentage(Math.round(percentage * 10.0) / 10.0);
            });
        }
        return dist;
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities() {
        List<ActivityWrapper> list = new ArrayList<>();

        // Lấy 5 bill
        List<Bill> recentBills = billRepository.findTop5ByOrderByDateDesc();
        for (Bill bill : recentBills) {
            list.add(new ActivityWrapper(
                    bill.getDate(), // thời gian thật
                    new RecentActivityDTO(
                            "bill",
                            "Hóa đơn #" + bill.getId() + " đã thanh toán",
                            getRelativeTime(bill.getDate())
                    )
            ));
        }

        // Lấy 5 member
        List<Member> recentMembers = memberRepository.findTop5ByOrderByJoinDateDesc();
        for (Member member : recentMembers) {
            list.add(new ActivityWrapper(
                    member.getJoinDate(), // thời gian thật
                    new RecentActivityDTO(
                            "member",
                            member.getFullName() + " đăng ký gói " + member.getMembership(),
                            getRelativeTime(member.getJoinDate())
                    )
            ));
        }

        return list.stream()
                .sorted((a, b) -> b.timestamp.compareTo(a.timestamp))
                .limit(5)
                .map(a -> a.dto)
                .collect(Collectors.toList());
    }

    private String getRelativeTime(java.util.Date date) {
        if (date == null) return "";
        long diff = System.currentTimeMillis() - date.getTime();
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 60) return minutes + " phút trước";
        if (hours < 24) return hours + " giờ trước";
        return days + " ngày trước";
    }

    private static class ActivityWrapper {
        Date timestamp;
        RecentActivityDTO dto;

        ActivityWrapper(Date timestamp, RecentActivityDTO dto) {
            this.timestamp = timestamp;
            this.dto = dto;
        }
    }
}
