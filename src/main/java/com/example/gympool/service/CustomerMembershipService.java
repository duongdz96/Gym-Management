package com.example.gympool.service;

import com.example.gympool.entity.MembershipRegister;
import com.example.gympool.entity.CustomerMembership;

import java.util.List;

public interface CustomerMembershipService {
    List<CustomerMembership> getAllCustomerMembership();
    CustomerMembership getMembershipById(Long id);
    CustomerMembership getMembershipByCustomerName(String customerName);
    CustomerMembership RegisterMembership(MembershipRegister membershipRegister);
    CustomerMembership updateMembership(Long id, CustomerMembership customerMembership);
}
