package com.example.gympool.service.impl;

import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.Member;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.MembershipTier;
import com.example.gympool.repository.CustomerMembershipRepository;
import com.example.gympool.repository.MembershipPlanRepository;
import com.example.gympool.repository.MembershipTierRepository;
import com.example.gympool.service.CustomerMembershipService;
import com.example.gympool.service.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerMembershipServiceImpl implements CustomerMembershipService {
    @Autowired
    private CustomerMembershipRepository customerMembershipRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Override
    public List<CustomerMembership> getAllCustomerMembership(){
        return customerMembershipRepository.findAll();
    }
    @Override
    public CustomerMembership getMembershipById(Long id){
        return customerMembershipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CustomerMembership not found with id: " + id));
    }
    @Override
    public CustomerMembership getMembershipByCustomerName(String customerName){
        return customerMembershipRepository.findByMemberName(customerName)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with name: " + customerName));
    }
    @Override
    public CustomerMembership getMembershipByEmail(String email) {
        return customerMembershipRepository.findByMemberEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }
    @Override
    @Transactional
    public CustomerMembership RegisterMembership(com.example.gympool.dto.CustomerMembershipRequest request){
        CustomerMembership customerMembership = new CustomerMembership();
        
        // Handle member - create new or use existing
        if (request.getMember() != null && request.getMember().getId() == null) {
            Member newMember = memberService.createMember(request.getMember());
            customerMembership.setMember(newMember);
        } else if (request.getMember() != null && request.getMember().getId() != null) {
            Member existingMember = memberService.getMemberById(request.getMember().getId());
            customerMembership.setMember(existingMember);
        }

        // Handle membership plan
        if (request.getMembershipPlanId() != null) {
            com.example.gympool.entity.MembershipPlan plan = membershipPlanRepository.findById(request.getMembershipPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("Membership Plan not found"));
            customerMembership.setMembershipPlan(plan);
        }

        // Set dates
        customerMembership.setStartDate(request.getStartDate());
        customerMembership.setEndDate(request.getEndDate());
        
        // Set default status
        customerMembership.setStatus("Active");

        CustomerMembership savedMembership = customerMembershipRepository.save(customerMembership);

        // Update Member info
        Member member = savedMembership.getMember();
        if (member != null) {
            if (savedMembership.getMembershipPlan() != null && savedMembership.getMembershipPlan().getMembershipTier() != null) {
                member.setMembership(savedMembership.getMembershipPlan().getMembershipTier().getName());
            }
            member.setStatus("Active");
            if (member.getJoinDate() == null) {
                member.setJoinDate(new java.util.Date());
            }
            memberService.updateMember(member.getId(), member);
        }

        return savedMembership;
    }
    @Override
    public CustomerMembership updateMembership(Long id, CustomerMembership customerMembershipDetails){
        CustomerMembership customerMembership = getMembershipById(id);

        // update field từ User
        if (customerMembershipDetails.getMembershipPlan() != null) customerMembership.setMembershipPlan(customerMembershipDetails.getMembershipPlan());
        if (customerMembershipDetails.getStatus() != null) customerMembership.setStatus(customerMembershipDetails.getStatus());
        if (customerMembershipDetails.getMember() != null) customerMembership.setMember(customerMembershipDetails.getMember());
        if (customerMembershipDetails.getStartDate() != null) customerMembership.setStartDate(customerMembershipDetails.getStartDate());
        if (customerMembershipDetails.getEndDate() != null) customerMembership.setEndDate(customerMembershipDetails.getEndDate());
        return customerMembershipRepository.save(customerMembership);
    }
    @Override
    @Transactional
    public CustomerMembership renewMembership(Long currentMembershipId, Long newPlanId) {
        CustomerMembership current = getMembershipById(currentMembershipId);
        MembershipPlan newPlan = membershipPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Date today = new Date();
        Date baseDate = current.getEndDate().after(today) ? current.getEndDate() : today;
        Date newEndDate = addDuration(baseDate, newPlan.getDuration());

        if (current.getEndDate().before(today)) {
            current.setStartDate(today);
        }

        current.setMembershipPlan(newPlan);
        current.setEndDate(newEndDate);
        current.setStatus("Active");

        return customerMembershipRepository.save(current);
    }

    //Tuyetzz
    //cái này sẽ lấy cái cũ chia cho số tiền
    //ví du
    //gói silver (1 thang 60k) còn 2 ngày, đơn giá 2k/ngày => Dư 4k
    //gói gold mới (1 thang 90k) => đơn giá 3k/ngày
    //lấy 4k / 3k = 1.33 ngày
    //enddate += 1.33 ngày
    @Override
    @Transactional
    public CustomerMembership upgradeMembership(Long currentMembershipId, Long newPlanId) {
        CustomerMembership oldMem = getMembershipById(currentMembershipId);
        MembershipPlan newPlan = membershipPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Date today = new Date();

        // 1. Tính ngày kết thúc tiêu chuẩn (Hôm nay + Thời hạn gói mới)
        Date standardEndDate = addDuration(today, newPlan.getDuration());
        Date finalEndDate = standardEndDate;

        // 2. Tính toán quy đổi nếu gói cũ còn hạn
        if (oldMem.getEndDate().after(today)) {
            // Tính số ngày còn lại của gói cũ
            long diffInMillies = oldMem.getEndDate().getTime() - today.getTime();
            long oldDaysRemaining = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            // Tính ngày/giờ của gói cũ
            long oldDurationDays = getDurationInDays(oldMem.getMembershipPlan().getDuration());
            double oldPricePerDay = oldMem.getMembershipPlan().getPrice() / oldDurationDays;

            // Tính tiền dư
            double residualValue = oldDaysRemaining * oldPricePerDay;

            // Tính đơn giá ngày của gói mới
            long newDurationDays = getDurationInDays(newPlan.getDuration());
            double newPricePerDay = newPlan.getPrice() / newDurationDays;

            // Tính số ngày được cộng thêm
            double extraDays = residualValue / newPricePerDay;

            // Cộng thêm số ngày này vào hạn chuẩn (đổi ra milliseconds để chính xác cả số lẻ như 1.33 ngày)
            long extraTimeInMillis = (long) (extraDays * 24 * 60 * 60 * 1000);
            finalEndDate = new Date(standardEndDate.getTime() + extraTimeInMillis);
        }

        oldMem.setStatus("Upgraded");
        customerMembershipRepository.save(oldMem);

        CustomerMembership newMem = new CustomerMembership();
        newMem.setMember(oldMem.getMember());
        newMem.setMembershipPlan(newPlan);
        newMem.setStartDate(today);
        newMem.setEndDate(finalEndDate);
        newMem.setStatus("Active");

        Member m = oldMem.getMember();
        if (m != null) {
            m.setMembership(newPlan.getMembershipTier().getName());
            memberService.updateMember(m.getId(), m);
        }

        return customerMembershipRepository.save(newMem);
    }

    private Date addDuration(Date startDate, String duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        String[] parts = duration.split(" ");
        int amount = Integer.parseInt(parts[0]);
        String unit = parts[1].toLowerCase();

        if (unit.contains("month")) {
            cal.add(Calendar.MONTH, amount);
        } else if (unit.contains("year")) {
            cal.add(Calendar.YEAR, amount);
        }
        return cal.getTime();
    }

    private long getDurationInDays(String duration) {
        Date now = new Date();
        Date end = addDuration(now, duration);
        return (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
    }

    @Override
    public CustomerMembership getLatestMembership(Long memberId) {
        Optional<CustomerMembership> lastMemOpt = customerMembershipRepository.findFirstByMemberIdOrderByIdDesc(memberId);

        if (lastMemOpt.isEmpty()) {
            return null;
        }

        CustomerMembership membership = lastMemOpt.get();

        if ("Active".equalsIgnoreCase(membership.getStatus())) {
            Date now = new Date();
            if (membership.getEndDate().before(now)) {
                membership.setStatus("Expired");
                return customerMembershipRepository.save(membership);
            }
        }

        return membership;
    }

    @Override
    @Transactional
    public void scanAndExpireMemberships() {
        customerMembershipRepository.updateExpiredMemberships(new Date());
    }
}
