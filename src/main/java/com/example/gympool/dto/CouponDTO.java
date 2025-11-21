package com.example.gympool.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponDTO {
    private Long id;
    private String code;
    private String discountType;
    private double discountValue;
    private String status;
    private String scope;
    private int totalUses;


}