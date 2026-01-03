package com.example.gympool.service;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AccessLogService {
    List<AccessLog> getAllAccessLogs(Long memberId);
    List<AccessLog> getAccessLogsByTime(Long memberId, LocalDate from, LocalDate to);
}
