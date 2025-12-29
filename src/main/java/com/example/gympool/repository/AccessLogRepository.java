package com.example.gympool.repository;

import com.example.gympool.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    // Lấy log theo member
    List<AccessLog> findByMemberId(Long memberId);

    // Lấy log theo result
    List<AccessLog> findByResult(String result);
    
    // Query method for Reception Dashboard
    @Query("SELECT COUNT(al) FROM AccessLog al WHERE DATE(al.accessTime) = CURRENT_DATE AND al.result = 'SUCCESS'")
    Long countTodayCheckIns();
}
