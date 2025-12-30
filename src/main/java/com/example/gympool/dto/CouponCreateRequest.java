package com.example.gympool.dto;

import com.example.gympool.entity.Coupon;
import lombok.Data;

import java.util.List;

@Data
public class CouponCreateRequest {
    private Coupon coupon;
    private List<Long> userIds;
}
