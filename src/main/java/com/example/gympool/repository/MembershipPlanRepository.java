package com.example.gympool.repository;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.MembershipTier;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findAllByStatus(String status);
    Optional<MembershipPlan> findByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE MembershipPlan m SET m.status = :status WHERE m.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") String status);
}
