package com.example.gympool.controller;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membershipplan")
@RequiredArgsConstructor
public class MembershipPlanController {
    private final MembershipPlanService membershipPlanService;
    @GetMapping()
    public List<MembershipPlan> findAll() {
        return membershipPlanService.getAllMembershipPlan();
    }
    @GetMapping("/{id}")
    public MembershipPlan findMembershipPlanById(@PathVariable("id") Long id) {
        return membershipPlanService.getMembershipPlanById(id);
    }
    @PostMapping()
    public void addMembershipPlan(@RequestBody MembershipPlan MembershipPlan) {
        membershipPlanService.addMembershipPlan(MembershipPlan);
    }

    @PutMapping("/{id}")
    public void updateMembershipPlan(@PathVariable("id") Long id,
                                     @RequestBody MembershipPlan MembershipPlan) {
        membershipPlanService.updateMembershipPlan(id, MembershipPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MembershipPlan> deleteMembershipPlan(@PathVariable Long id) {
        MembershipPlan deletedPlan = membershipPlanService.deleteMembershipPlan(id);
        return ResponseEntity.ok(deletedPlan);
    }

    // /api/membership-plans/1/status?status=Inactive
    @PatchMapping("/{id}/status")
    public ResponseEntity<MembershipPlan> changeStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        MembershipPlan updatedPlan = membershipPlanService.changeStatusMembershipPlan(id, status);
        return ResponseEntity.ok(updatedPlan);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MembershipPlan>> getAllActivePlans() {
        List<MembershipPlan> activePlans = membershipPlanService.getAllActiveMembershipPlans();
        return ResponseEntity.ok(activePlans);
    }
}
