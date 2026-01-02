package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembershipDistributionDTO {
    private String tier; // Basic, Premium, VIP
    private Long count;
    private Double percentage;
    public MembershipDistributionDTO(String tier, Long count, int percentage) {
        this.tier = tier;
        this.count = count;
        this.percentage = (double) percentage;
    }
}
