package com.example.gympool.service;

import com.example.gympool.entity.TrainingPlanDetails;

public interface TrainingPlanDetailsService {
    TrainingPlanDetails toggleStatus(Long id);
}
