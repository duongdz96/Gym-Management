package com.example.gympool.dto;

import lombok.Data;

import java.util.List;

@Data
public class CancelRegistrationRequest {
    private Long memberId;
    private List<Long> registrationIds;
}
