package com.example.gympool.service.impl;

import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.Member;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.MembershipTier;
import com.example.gympool.repository.CustomerMembershipRepository;
import com.example.gympool.repository.MembershipTierRepository;
import com.example.gympool.service.CustomerMembershipService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service
public class CustomerMembershipServiceImpl implements CustomerMembershipService {
    private final CustomerMembershipRepository customerMembershipRepository;
    private final com.example.gympool.service.MemberService memberService;
    private final com.example.gympool.repository.MembershipPlanRepository membershipPlanRepository;

    public CustomerMembershipServiceImpl(CustomerMembershipRepository customerMembershipRepository, 
                                         com.example.gympool.service.MemberService memberService,
                                         com.example.gympool.repository.MembershipPlanRepository membershipPlanRepository) {
        this.customerMembershipRepository = customerMembershipRepository;
        this.memberService = memberService;
        this.membershipPlanRepository = membershipPlanRepository;
    }
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

        Date baseDate = current.getEndDate().after(new Date()) ? current.getEndDate() : new Date();

        Date newEndDate = addDuration(baseDate, newPlan.getDuration());

        current.setMembershipPlan(newPlan);
        current.setEndDate(newEndDate);
        current.setStatus("Active");

        return customerMembershipRepository.save(current);
    }

    //Tuyetzz
    //cái này sẽ lấy cái cũ chia cho số tiền
    //ví du
    //gói silver còn 2 ngày, đơn giá 2k/ngày => Dư 4k
    //gói gold mới đơn giá 3k/ngày
    //lấy 4k / 3k = 1.33 ngày
    //enddate += 1.33 ngày
    @Override
    @Transactional
    public CustomerMembership upgradeMembership(Long currentMembershipId, Long newPlanId) {
        CustomerMembership oldMem = getMembershipById(currentMembershipId);
        MembershipPlan newPlan = membershipPlanRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Date today = new Date();
        //ngày còn lại
        long diffInMillies = oldMem.getEndDate().getTime() - today.getTime();
        long daysRemaining = Math.max(0, diffInMillies / (1000 * 60 * 60 * 24));

        if (daysRemaining > 0) {
            long totalDaysOld = getDurationInDays(oldMem.getMembershipPlan().getDuration());    //lay so ngay con lai
            double pricePerDayOld = oldMem.getMembershipPlan().getPrice() / totalDaysOld;  //gia cu (1 ngày)
            double refundValue = daysRemaining * pricePerDayOld;    //tinh tien con du (2 ngay * 2k = 4k)

            long totalDaysNew = getDurationInDays(newPlan.getDuration());   //lay so ngay moi
            double pricePerDayNew =  newPlan.getPrice() / totalDaysNew; //gia moi (1 ngày)
            double extraDays = refundValue / pricePerDayNew;   // 4/3 -> 1.333

            long extraMillis = (long) (extraDays * 24 * 60 * 60 * 1000);

            Date standardEndDate = addDuration(today, newPlan.getDuration());
            Date finalEndDate = new Date(standardEndDate.getTime() + extraMillis);

            oldMem.setStatus("Upgraded");
            customerMembershipRepository.save(oldMem);

            CustomerMembership newMem = new CustomerMembership();
            newMem.setMember(oldMem.getMember());
            newMem.setMembershipPlan(newPlan);
            newMem.setStartDate(today);
            newMem.setEndDate(finalEndDate);
            newMem.setStatus("Active"); //để im cái cũ mà ko xoa, se tao cai moi

            Member m = oldMem.getMember();
            if (m != null) {
                m.setMembership(newPlan.getMembershipTier().getName());
                memberService.updateMember(m.getId(), m);
            }

            return customerMembershipRepository.save(newMem);
        }

        return renewMembership(currentMembershipId, newPlanId);
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
}
