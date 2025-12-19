package com.example.gympool.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GeneratePlanRequest {
    private Long routineId;
    private Long memberId;
    private LocalDate date;
}