package com.example.gympool.service.impl;

import com.example.gympool.dto.*;
import com.example.gympool.entity.Bill;
import com.example.gympool.entity.Member;
import com.example.gympool.entity.Product;
import com.example.gympool.repository.*;
import com.example.gympool.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Autowired
    private ImportBillRepository importBillRepository;
    @Autowired
    private AccessLogRepository accessLogRepository;
    @Autowired
    private ClassScheduleRepository classScheduleRepository;
    @Autowired
    private ClassRegistrationRepository classRegistrationRepository;

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
        List<Object[]> results = billRepository.getMonthlyRevenue(year);
        
        // Tạo map để dễ tra cứu
        java.util.Map<Integer, Double> revenueMap = new java.util.HashMap<>();
        for (Object[] row : results) {
            if (row[0] != null && row[1] != null) {
                // Sử dụng Number để an toàn với các kiểu dữ liệu khác nhau (Long, Integer, BigDecimal, Double)
                int month = ((Number) row[0]).intValue();
                double revenue = ((Number) row[1]).doubleValue();
                revenueMap.put(month, revenue);
            }
        }

        List<MonthlyRevenueDTO> fullYearData = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Double revenue = revenueMap.getOrDefault(i, 0.0);
            fullYearData.add(new MonthlyRevenueDTO("T" + i, revenue));
        }
        
        return fullYearData;
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

        // Lấy 5 import bill (recent products section updated)
        List<com.example.gympool.entity.ImportBill> recentImportBills = importBillRepository.findTop5ByOrderByDateDesc();
        for (com.example.gympool.entity.ImportBill bill : recentImportBills) {
            list.add(new ActivityWrapper(
                    bill.getDate(),
                    new RecentActivityDTO(
                            "product",
                            "Phiếu nhập #" + bill.getId() + " - " + (bill.getProvider() != null ? bill.getProvider().getName() : "Unknown"),
                            getRelativeTime(bill.getDate())
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
    
    // ===================== RECEPTION DASHBOARD METHODS =====================
    
    @Override
    public ReceptionTodayStatsDTO getReceptionTodayStats() {
        ReceptionTodayStatsDTO stats = new ReceptionTodayStatsDTO();
        
        // Get new memberships today
        Long newMemberships = customerMembershipRepository.countTodayNewMemberships();
        stats.setNewMemberships(newMemberships != null ? newMemberships : 0L);
        
        // Get total check-ins today
        Long checkIns = accessLogRepository.countTodayCheckIns();
        stats.setTotalCheckIns(checkIns != null ? checkIns : 0L);
        
        // Get total revenue today
        Double revenue = billRepository.getTodayRevenue();
        stats.setTotalRevenue(revenue != null ? revenue : 0.0);
        
        return stats;
    }
    
    @Override
    public List<RecentTransactionDTO> getRecentTransactions() {
        List<Bill> todayBills = billRepository.findTodayBills();
        
        return todayBills.stream()
                .limit(5)
                .map(bill -> {
                    RecentTransactionDTO dto = new RecentTransactionDTO();
                    dto.setId(bill.getId());
                    
                    // Get customer name from member
                    String customerName = "Khách hàng";
                    if (bill.getMember() != null && bill.getMember().getFullName() != null) {
                        customerName = bill.getMember().getFullName();
                    }
                    dto.setCustomerName(customerName);
                    
                    // Determine transaction type based on bill contents
                    String type = "Sản phẩm";
                    if (bill.getListSoldProduct() != null && !bill.getListSoldProduct().isEmpty()) {
                        // Check if it's a membership or PT package by looking at product names
                        boolean hasMembership = bill.getListSoldProduct().stream()
                                .anyMatch(sp -> sp.getProduct() != null && 
                                        sp.getProduct().getName() != null &&
                                        (sp.getProduct().getName().toLowerCase().contains("gói") ||
                                         sp.getProduct().getName().toLowerCase().contains("membership")));
                        boolean hasPT = bill.getListSoldProduct().stream()
                                .anyMatch(sp -> sp.getProduct() != null && 
                                        sp.getProduct().getName() != null &&
                                        (sp.getProduct().getName().toLowerCase().contains("pt") ||
                                         sp.getProduct().getName().toLowerCase().contains("personal training")));
                        
                        if (hasMembership) {
                            type = "Gói thành viên";
                        } else if (hasPT) {
                            type = "PT Package";
                        }
                    }
                    dto.setType(type);
                    
                    dto.setAmount(bill.getTotal());
                    dto.setStatus(bill.getPaymentStatus());
                    
                    // Format time as HH:mm
                    if (bill.getDate() != null) {
                        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
                        dto.setTime(timeFormat.format(bill.getDate()));
                    } else {
                        dto.setTime("--:--");
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UpcomingClassDTO> getUpcomingClasses() {
        // Get classes starting in the next 7 days
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(7);
        
        List<com.example.gympool.entity.ClassSchedule> upcomingSchedules = 
                classScheduleRepository.findUpcomingClasses(endDate);
        
        return upcomingSchedules.stream()
                .limit(5)
                .map(schedule -> {
                    UpcomingClassDTO dto = new UpcomingClassDTO();
                    
                    // Get class name
                    String className = "Lớp học";
                    if (schedule.getFitnessClass() != null && schedule.getFitnessClass().getName() != null) {
                        className = schedule.getFitnessClass().getName();
                    }
                    dto.setClassName(className);
                    
                    // Get teacher name - try to find from ClassRegistration
                    String teacherName = "Chưa có giáo viên";
                    if (schedule.getFitnessClass() != null) {
                        List<com.example.gympool.entity.ClassRegistration> registrations = 
                                classRegistrationRepository.findByFitnessClass(schedule.getFitnessClass());
                        if (!registrations.isEmpty()) {
                            // Get the first approved teacher
                            for (com.example.gympool.entity.ClassRegistration reg : registrations) {
                                if ("APPROVED".equals(reg.getStatus()) && reg.getTeacher() != null) {
                                    teacherName = reg.getTeacher().getFullName();
                                    break;
                                }
                            }
                        }
                    }
                    dto.setTeacher(teacherName);
                    
                    // Format start date as dd/MM/yyyy
                    if (schedule.getStartTime() != null) {
                        java.time.format.DateTimeFormatter dateFormatter = 
                                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        dto.setStartDate(schedule.getStartTime().format(dateFormatter));
                        
                        // Calculate days left
                        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(
                                LocalDateTime.now().toLocalDate(), 
                                schedule.getStartTime().toLocalDate()
                        );
                        dto.setDaysLeft(daysLeft);
                    } else {
                        dto.setStartDate("--/--/----");
                        dto.setDaysLeft(0L);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
