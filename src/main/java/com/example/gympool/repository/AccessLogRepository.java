package com.example.gympool.repository;

import com.example.gympool.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    // Lấy log theo member
    List<AccessLog> findByMemberId(Long memberId);

    @Query("SELECT sf FROM AccessLog sf WHERE sf.member.id = :memberId and sf.accessTime >= :from and sf.accessTime< :to")
    List<AccessLog> findByAccessTime(Long memberId,
                                     LocalDateTime from,
                                     LocalDateTime to);

    // Lấy log theo result
    List<AccessLog> findByResult(String result);

    // Query method for Reception Dashboard
    @Query("SELECT COUNT(al) FROM AccessLog al WHERE DATE(al.accessTime) = CURRENT_DATE ")
    Long countTodayCheckIns();

}
