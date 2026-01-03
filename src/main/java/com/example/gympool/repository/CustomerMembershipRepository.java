package com.example.gympool.repository;

import com.example.gympool.dto.MembershipDistributionDTO;
import com.example.gympool.entity.CustomerMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerMembershipRepository extends JpaRepository<CustomerMembership, Long> {
    @Query("SELECT cm FROM CustomerMembership cm WHERE cm.member.fullName LIKE %:name%")
    Optional<CustomerMembership> findByMemberName(String name);
    
    List<CustomerMembership> findByMembershipPlan_MembershipTier_Name(String tiername);

    @Query("SELECT new com.example.gympool.dto.MembershipDistributionDTO(" +
            "cm.membershipPlan.membershipTier.name, COUNT(cm), 0.0) " +
            "FROM CustomerMembership cm " +
            "WHERE cm.status = 'Active' " +
            "GROUP BY cm.membershipPlan.membershipTier.name")
    List<MembershipDistributionDTO> getMembershipDistribution();

    Optional<CustomerMembership> findFirstByMemberIdOrderByIdDesc(Long memberId);

    @Modifying
    @Query("UPDATE CustomerMembership c SET c.status = 'Expired' WHERE c.status = 'Active' AND c.endDate < :now")
    void updateExpiredMemberships(@Param("now") Date now);
    
    // Query method for Reception Dashboard
    @Query("SELECT COUNT(cm) FROM CustomerMembership cm WHERE DATE(cm.startDate) = CURRENT_DATE")
    Long countTodayNewMemberships();
}
