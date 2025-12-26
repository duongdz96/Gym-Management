package com.example.gympool.service;

import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.MembershipPlan;

import java.util.List;
import java.util.Optional;

public interface CustomerMembershipService {
    List<CustomerMembership> getAllCustomerMembership();
    CustomerMembership getMembershipById(Long id);
    CustomerMembership getMembershipByCustomerName(String customerName);
    CustomerMembership RegisterMembership(com.example.gympool.dto.CustomerMembershipRequest request);
    CustomerMembership updateMembership(Long id, CustomerMembership customerMembership);

    //gia han
    CustomerMembership renewMembership(Long currentMembershipId, Long newPlanId);
    //doi goi
    CustomerMembership upgradeMembership(Long currentMembershipId, Long newPlanId);
    //theo id cua user
    CustomerMembership getLatestMembership(Long memberId);
}
