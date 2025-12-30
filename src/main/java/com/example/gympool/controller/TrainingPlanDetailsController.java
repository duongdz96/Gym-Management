package com.example.gympool.controller;

import com.example.gympool.entity.TrainingPlanDetails;
import com.example.gympool.service.TrainingPlanDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/training-plan-details")
public class TrainingPlanDetailsController {

    private final TrainingPlanDetailsService trainingPlanDetailsService;
    public TrainingPlanDetailsController(TrainingPlanDetailsService trainingPlanDetailsService) {
        this.trainingPlanDetailsService = trainingPlanDetailsService;
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<TrainingPlanDetails> toggleStatus(@PathVariable Long id) {
        TrainingPlanDetails updatedDetail = trainingPlanDetailsService.toggleStatus(id);
        return ResponseEntity.ok(updatedDetail);
    }
}