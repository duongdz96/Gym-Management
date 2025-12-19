package com.example.gympool.controller;

import com.example.gympool.dto.GeneratePlanRequest;
import com.example.gympool.entity.TrainingPlans;
import com.example.gympool.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlansController {

    @Autowired
    private TrainingPlanService trainingPlanService;

    @PostMapping("/generate")
    public ResponseEntity<TrainingPlans> generateFromRoutine(@RequestBody GeneratePlanRequest request) {
        TrainingPlans plan = trainingPlanService.createPlanFromRoutine(
                request.getRoutineId(),
                request.getMemberId(),
                request.getDate()
        );
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    // 2. Lấy lịch tập hôm nay của một Member
    @GetMapping("/today/{memberId}")
    public ResponseEntity<TrainingPlans> getTodayPlan(@PathVariable Long memberId) {
        TrainingPlans plan = trainingPlanService.getTodayPlan(memberId);
        if (plan != null) {
            return ResponseEntity.ok(plan);
        }
        return ResponseEntity.noContent().build();
    }

    // 3. Xem lịch sử tập luyện của Member
    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<TrainingPlans>> getMemberHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(trainingPlanService.getMemberHistory(memberId));
    }

    // 4. Cập nhật trạng thái buổi tập (VD: Chuyển từ PENDING sang COMPLETED)
    @PatchMapping("/{id}/status")
    public ResponseEntity<TrainingPlans> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(trainingPlanService.updateStatus(id, status));
    }


}