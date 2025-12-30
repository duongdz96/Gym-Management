package com.example.gympool.service.impl;

import com.example.gympool.entity.TrainingPlanDetails;
import com.example.gympool.repository.TrainingPlanDetailsRepository;
import com.example.gympool.service.TrainingPlanDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingPlanDetailsServiceImpl implements TrainingPlanDetailsService {

    @Autowired
    private TrainingPlanDetailsRepository repo;

    @Override
    public TrainingPlanDetails toggleStatus(Long id) {
        TrainingPlanDetails detail = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài tập"));

        String currentStatus = detail.getStatus();

        if (TrainingPlanDetails.STATUS_COMPLETED.equals(currentStatus)) {
            detail.setStatus(TrainingPlanDetails.STATUS_PENDING);
        } else {
            detail.setStatus(TrainingPlanDetails.STATUS_COMPLETED);
        }

        return repo.save(detail);
    }
}