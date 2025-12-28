package com.example.gympool.service.impl;

import com.example.gympool.entity.MembershipRegister;
import com.example.gympool.entity.*;

import com.example.gympool.repository.CustomerMembershipRepository;
import com.example.gympool.repository.MemberRepository;
import com.example.gympool.repository.MembershipPlanRepository;
import com.example.gympool.service.CustomerMembershipService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CustomerMembershipServiceImpl implements CustomerMembershipService {
    private final CustomerMembershipRepository customerMembershipRepository;
    private final MemberRepository memberRepository;
    private final MembershipPlanRepository membershipPlanRepository;
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
    public CustomerMembership RegisterMembership(MembershipRegister  membershipRegister){
        if(memberRepository.findByEmail(membershipRegister.getEmail()).isPresent()){
            throw new IllegalArgumentException("Member already exists with email: " + membershipRegister.getEmail());
        }
        if (memberRepository.findByPhone(membershipRegister.getPhone()).isPresent()){
            throw new IllegalArgumentException("Member already exists with phone: " + membershipRegister.getPhone());
        }
        Member member = new Member();
        member.setEmail(membershipRegister.getEmail());
        member.setPhone(membershipRegister.getPhone());
        member.setPassword(membershipRegister.getPassword());
        member.setFullName(membershipRegister.getFullName());
        member.setGender(membershipRegister.getGender());
        member.setDob(membershipRegister.getDob());
        memberRepository.save(member);
        MembershipPlan membershipPlan = membershipPlanRepository.findById(membershipRegister.getPlanId())
        .orElseThrow(() -> new IllegalArgumentException("Membership plan not found with id: " + membershipRegister.getPlanId()));
        CustomerMembership customerMembership = new CustomerMembership();
        customerMembership.setStatus(String.valueOf(Status.ACTIVE));
        customerMembership.setMember(member);
        customerMembership.setMembershipPlan(membershipPlan);
        customerMembership.setStartDate(new Date());
        customerMembership.setEndDate(calculateEndDate(new Date(), membershipPlan.getDuration()));
        return customerMembershipRepository.save(customerMembership);
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

    private Date calculateEndDate(Date startDate, Integer duration){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, duration);
        return calendar.getTime();
    }
}
