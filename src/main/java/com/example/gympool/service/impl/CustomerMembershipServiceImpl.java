package com.example.gympool.service.impl;

import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.Member;

import com.example.gympool.entity.MembershipTier;
import com.example.gympool.repository.CustomerMembershipRepository;
import com.example.gympool.repository.MembershipTierRepository;
import com.example.gympool.service.CustomerMembershipService;
import org.springframework.stereotype.Service;

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
    @org.springframework.transaction.annotation.Transactional
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
}
