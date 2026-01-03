package com.example.gympool.repository;

import com.example.gympool.entity.TrainingPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingPlansRepository extends JpaRepository<TrainingPlans, Long> {
    List<TrainingPlans> findByMemberId(Long memberId);
    Optional<TrainingPlans> findByMemberIdAndDate(Long memberId, LocalDate date);
    List<TrainingPlans> findByMemberIdAndStatus(Long memberId, String status);
}
