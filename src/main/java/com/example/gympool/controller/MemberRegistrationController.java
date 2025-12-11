package com.example.gympool.controller;

import com.example.gympool.dto.CancelRegistrationRequest;
import com.example.gympool.dto.ClassRegistrationRequest;
import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.service.MemberRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-registrations")
public class MemberRegistrationController {

    private final MemberRegistrationService memberRegistrationService;

    public MemberRegistrationController(MemberRegistrationService memberRegistrationService) {
        this.memberRegistrationService = memberRegistrationService;
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<MemberRegistration>> getByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberRegistrationService.getByMember(memberId));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<MemberRegistration>> getByClassSchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(memberRegistrationService.getByClassSchedule(scheduleId));
    }

    @PostMapping
    public ResponseEntity<?> registerClass(@RequestBody MemberRegistration memberRegistration) {
        try {
            Long memberId = memberRegistration.getMember().getId();
            Long scheduleId = memberRegistration.getClassSchedule().getId();
            return ResponseEntity.ok(memberRegistrationService.registerForClass(memberId, scheduleId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelRegistration(@PathVariable("id") Long registrationId, @RequestParam Long memberId) {
        try {
            memberRegistrationService.cancelRegistration(registrationId, memberId);
            return ResponseEntity.ok("Cancelled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/bulk-register")
    public ResponseEntity<?> registerBulkClass(@RequestBody ClassRegistrationRequest request) {
        try {
            List<MemberRegistration> results = memberRegistrationService.registerBulk(
                    request.getMemberId(),
                    request.getScheduleIds()
            );
            return ResponseEntity.ok(results);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Đăng ký thất bại: " + e.getMessage());
        }
    }

    @DeleteMapping("/bulk-cancel")
    public ResponseEntity<?> cancelBulkClass(@RequestBody CancelRegistrationRequest request) {
        try {
            memberRegistrationService.cancelBulkRegistration(
                    request.getRegistrationIds(),
                    request.getMemberId()
            );
            return ResponseEntity.ok("Đã hủy thành công các lịch đăng ký.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}