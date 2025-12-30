package com.example.gympool.controller;

import com.example.gympool.entity.Coupon;
import com.example.gympool.entity.IssuedCoupon;
import com.example.gympool.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkCoupon(
            @RequestParam String code,
            @RequestParam Long memberId) {

        Optional<Coupon> couponOpt = couponService.getValidCouponForMember(code, memberId);

        if (couponOpt.isPresent()) {
            return ResponseEntity.ok(couponOpt.get());
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponDetail(@PathVariable Long id) {
        Optional<Coupon> couponOpt = couponService.getCouponById(id);

        if (couponOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy coupon với id: " + id);
        }

        Coupon coupon = couponOpt.get();
        List<IssuedCoupon> issuedList = couponService.getIssuedCouponsByCouponId(coupon);

        Map<String, Object> response = new HashMap<>();
        response.put("coupon", coupon);
        response.put("issuedCoupons", issuedList);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createCoupon(@RequestBody com.example.gympool.dto.CouponCreateRequest request) {
        try {
            couponService.createCouponAndIssueToMembers(request.getCoupon(), request.getUserIds());
            return ResponseEntity.ok("Coupon created and issued successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        Coupon updatedCoupon = couponService.updateCoupon(id, coupon);
        return ResponseEntity.ok(updatedCoupon);
    }


    @PostMapping("/import")
    public ResponseEntity<?> importCoupons(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            String content = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
            String[] lines = content.split("\\r?\\n");
            
            int successCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();

            // Skip header line
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                try {
                    String[] fields = line.split(",");
                    if (fields.length < 7) {
                        errors.append("Line ").append(i + 1).append(": Invalid format\n");
                        errorCount++;
                        continue;
                    }

                    Coupon coupon = new Coupon();
                    coupon.setCode(fields[0].trim());
                    coupon.setDiscountType(fields[1].trim());
                    coupon.setDiscountValue(Double.parseDouble(fields[2].trim()));
                    
                    // Parse dates - try multiple formats
                    String startDateStr = fields[3].trim();
                    String endDateStr = fields[4].trim();
                    
                    java.text.SimpleDateFormat[] formats = {
                        new java.text.SimpleDateFormat("yyyy-MM-dd"),
                        new java.text.SimpleDateFormat("M/d/yyyy"),
                        new java.text.SimpleDateFormat("MM/dd/yyyy"),
                        new java.text.SimpleDateFormat("d/M/yyyy"),
                        new java.text.SimpleDateFormat("dd/MM/yyyy")
                    };
                    
                    Date startDate = null;
                    Date endDate = null;
                    
                    for (java.text.SimpleDateFormat format : formats) {
                        try {
                            startDate = format.parse(startDateStr);
                            break;
                        } catch (Exception ignored) {}
                    }
                    
                    for (java.text.SimpleDateFormat format : formats) {
                        try {
                            endDate = format.parse(endDateStr);
                            break;
                        } catch (Exception ignored) {}
                    }
                    
                    if (startDate == null || endDate == null) {
                        errors.append("Line ").append(i + 1).append(": Invalid date format\n");
                        errorCount++;
                        continue;
                    }
                    
                    coupon.setStartDate(startDate);
                    coupon.setEndDate(endDate);
                    coupon.setStatus(fields[5].trim());
                    coupon.setScope(fields[6].trim());

                    couponService.createCouponAndIssueToMembers(coupon, new java.util.ArrayList<>());
                    successCount++;
                } catch (Exception e) {
                    errors.append("Line ").append(i + 1).append(": ").append(e.getMessage()).append("\n");
                    errorCount++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", successCount);
            result.put("errors", errorCount);
            result.put("errorDetails", errors.toString());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Import failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
