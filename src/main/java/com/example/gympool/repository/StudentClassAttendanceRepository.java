package com.example.gympool.repository;

import com.example.gympool.entity.StudentClassAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentClassAttendanceRepository extends JpaRepository<StudentClassAttendance, Long> {
    Optional<StudentClassAttendance> findByClassScheduleIdAndMemberId(Long classScheduleId, Long memberId);
    List<StudentClassAttendance> findAllByClassScheduleId(Long classScheduleId);
}
