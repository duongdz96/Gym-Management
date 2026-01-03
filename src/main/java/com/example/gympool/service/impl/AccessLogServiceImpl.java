package com.example.gympool.service.impl;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Member;
import com.example.gympool.repository.AccessLogRepository;
import com.example.gympool.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class AccessLogServiceImpl implements AccessLogService {
    @Autowired
    private AccessLogRepository accessLogRepository;
    public List<AccessLog> getAllAccessLogs(Long memberId){
        return accessLogRepository.findByMemberId(memberId);
    }

    public List<AccessLog> getAccessLogsByTime(Long memberId, LocalDate from, LocalDate to){
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end   = to.plusDays(1).atStartOfDay();
        return accessLogRepository.findByAccessTime( memberId,start, end);
    }
}
