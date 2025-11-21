package com.example.gympool.controller;

import com.example.gympool.entity.Coupon;
import com.example.gympool.entity.IssuedCoupon;
import com.example.gympool.entity.Member;
import com.example.gympool.service.CouponService;
import com.example.gympool.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issued-coupons")
@RequiredArgsConstructor
public class IssuedCouponController {

    private final IssuedCouponService issuedCouponService;
    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<List<IssuedCoupon>> getAllIssuedCoupons() {
        return ResponseEntity.ok(issuedCouponService.getAllIssuedCoupons());
    }

    @GetMapping("/find")
    public ResponseEntity<List<IssuedCoupon>> getIssuedCoupons(@RequestParam Long memberId) {
        List<IssuedCoupon> list = issuedCouponService.getIssuedCouponsByMember(memberId);
        return ResponseEntity.ok(list);
    }


    @GetMapping("/coupon/{couponId}")
    public ResponseEntity<List<IssuedCoupon>> getIssuedCouponsByCoupon(@PathVariable Long couponId) {
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        return ResponseEntity.ok(issuedCouponService.getIssuedCouponsByCoupon(coupon));
    }

    @PostMapping
    public ResponseEntity<IssuedCoupon> createIssuedCoupon(@RequestBody IssuedCoupon issuedCoupon) {
        IssuedCoupon saved = issuedCouponService.issueCouponToMember(issuedCoupon);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssuedCoupon(@PathVariable Long id) {
        issuedCouponService.deleteIssuedCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
