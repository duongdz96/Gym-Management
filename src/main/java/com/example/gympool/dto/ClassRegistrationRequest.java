package com.example.gympool.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClassRegistrationRequest {
    private Long memberId;
    private List<Long> scheduleIds;
}
