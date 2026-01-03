package com.example.gympool.controller;

import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.service.CustomerMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class CustomerMembershipController {
    private final CustomerMembershipService customerMembershipService;
    @GetMapping()
    public List<CustomerMembership> findAll() {
        return customerMembershipService.getAllCustomerMembership();
    }
    @GetMapping("/{id}")
    public CustomerMembership findCustomerMembershipById(@PathVariable("id") Long id) {
        return customerMembershipService.getMembershipById(id);
    }
    @GetMapping("/")
    public CustomerMembership findCustomerMembershipByName(@RequestParam("name") String name) {
        return customerMembershipService.getMembershipByCustomerName(name);
    }
    @PostMapping()
    public void RegisterMembership(@RequestBody com.example.gympool.dto.CustomerMembershipRequest request) {
        customerMembershipService.RegisterMembership(request);
    }

    @PutMapping("/{id}")
    public void updateCustomerMembership(@PathVariable("id") Long id,
                                     @RequestBody CustomerMembership CustomerMembership) {
        customerMembershipService.updateMembership(id, CustomerMembership);
    }

    // API Gia hạn gói
    @PostMapping("/{id}/renew")
    public ResponseEntity<CustomerMembership> renewMembership(
            @PathVariable Long id,
            @RequestParam Long newPlanId) {
        CustomerMembership updated = customerMembershipService.renewMembership(id, newPlanId);
        return ResponseEntity.ok(updated);
    }

    // API Đổi gói / Nâng cấp gói
    @PostMapping("/{id}/upgrade")
    public ResponseEntity<CustomerMembership> upgradeMembership(
            @PathVariable Long id,
            @RequestParam Long newPlanId) {
        CustomerMembership upgraded = customerMembershipService.upgradeMembership(id, newPlanId);
        return ResponseEntity.ok(upgraded);
    }

    @GetMapping("/member/{memberId}/current")
    public ResponseEntity<CustomerMembership> getCurrentMembership(@PathVariable Long memberId) {
        CustomerMembership currentMem = customerMembershipService.getLatestMembership(memberId);

        if (currentMem == null) {
            return ResponseEntity.noContent().build(); // Trả về 204 No Content
        }

        return ResponseEntity.ok(currentMem);
    }
}


