package com.example.gympool.repository;

import com.example.gympool.entity.ClassSchedule;
import com.example.gympool.entity.ClassTemplate;
import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRegistrationRepository extends JpaRepository<MemberRegistration, Long> {
    List<MemberRegistration> findByMember(Member member);
    List<MemberRegistration> findByClassSchedule(ClassSchedule classSchedule);

    Optional<MemberRegistration> findByMemberAndClassSchedule(Member member, ClassSchedule classSchedule);
    @Query("SELECT COUNT(m) FROM MemberRegistration m WHERE m.classSchedule.id = :scheduleId")
    int countByClassScheduleId(@Param("scheduleId") Long scheduleId);

}
