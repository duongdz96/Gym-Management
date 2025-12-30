package com.example.gympool.controller;

import com.example.gympool.entity.AccessLog;
import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.entity.Member;
import com.example.gympool.service.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/accesslog")
@RequiredArgsConstructor
public class AccessLogController {
    private final AccessLogService accessLogService;
    @GetMapping("/{memberId}")
    public List<AccessLog> findAll(@PathVariable Long memberId) {
        return accessLogService.getAllAccessLogs(memberId);
    }
    @GetMapping("/{memberId}/time")
    public List<AccessLog> findByTime(@PathVariable Long memberId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return accessLogService.getAccessLogsByTime(memberId,from, to);
    }
}
