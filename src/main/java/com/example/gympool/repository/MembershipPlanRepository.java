package com.example.gympool.repository;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findAllByStatus(String status);
    Optional<MembershipPlan> findByName(String name);
}
