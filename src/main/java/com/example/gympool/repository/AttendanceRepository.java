package com.example.gympool.repository;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserId( Long memberId);

    @Query("SELECT sf FROM Attendance sf WHERE sf.user.id = :userId and sf.checkInTime >= :from and sf.checkInTime< :to")
    List<Attendance> findByTime(Long userId,
                                     LocalDateTime from,
                                     LocalDateTime to);
}
