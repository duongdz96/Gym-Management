package com.example.gympool.repository;

import com.example.gympool.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByFullName(String fullName);
    
    List<Member> findTop5ByOrderByJoinDateDesc();

    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 'Active'")
    Long countActiveMembers();

    @Query("SELECT COUNT(m) FROM Member m WHERE MONTH(m.joinDate) = :month AND YEAR(m.joinDate) = :year")
    Long countNewMembers(@Param("month") int month, @Param("year") int year);
}
