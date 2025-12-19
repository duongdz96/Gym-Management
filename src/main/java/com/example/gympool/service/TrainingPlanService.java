package com.example.gympool.service;

import com.example.gympool.entity.TrainingPlans;
import java.time.LocalDate;
import java.util.List;

public interface TrainingPlanService {
    TrainingPlans createPlanFromRoutine(Long routineId, Long memberId, LocalDate date);

    TrainingPlans savePlan(TrainingPlans plan);

    List<TrainingPlans> getMemberHistory(Long memberId);

    TrainingPlans getTodayPlan(Long memberId);

    TrainingPlans updateStatus(Long planId, String status);
}