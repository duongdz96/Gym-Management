package com.example.gympool.repository;

import com.example.gympool.entity.TrainingPlanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingPlanDetailsRepository extends JpaRepository<TrainingPlanDetails, Long> {
}
