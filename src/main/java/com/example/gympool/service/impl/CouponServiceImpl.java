package com.example.gympool.service.impl;

import com.example.gympool.entity.Coupon;
import com.example.gympool.entity.IssuedCoupon;
import com.example.gympool.entity.CustomerMembership;
import com.example.gympool.repository.CouponRepository;
import com.example.gympool.repository.IssuedCouponRepository;
import com.example.gympool.repository.CustomerMembershipRepository;
import com.example.gympool.repository.MemberRepository;
import com.example.gympool.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final CustomerMembershipRepository customerMembershipRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }


    @Override
    public Optional<Coupon> getValidCouponForMember(String code, Long memberId) {
        // Tìm coupon theo mã code
        Optional<Coupon> optionalCoupon = couponRepository.findByCode(code);
        if (optionalCoupon.isEmpty()) {
            return Optional.empty();
        }

        Coupon coupon = optionalCoupon.get();

        if (!memberRepository.existsById(memberId)) {
            return Optional.empty();
        }

        // Kiểm tra xem issued coupon có tồn tại với trạng thái AVAILABLE không
        Optional<IssuedCoupon> issued = issuedCouponRepository
                .findByCouponIdAndMemberIdAndStatus(coupon.getId(), memberId, "AVAILABLE");

        if(issued.isPresent()) {
            IssuedCoupon issuedCoupon = issued.get();

            if(issuedCoupon.getRemainingUses() != null && issuedCoupon.getRemainingUses() > 0) {
                return Optional.of(coupon);
            }
        }

            return Optional.empty();
    }


    @Override
    public void createCouponAndIssueToMembers(Coupon couponRequest, List<Long> userIds) {
        // 1. Lưu coupon
        Coupon savedCoupon = couponRepository.save(couponRequest);
        
        // Default totalUses to 3 if not set
        Integer totalUses = savedCoupon.getTotalUses() != null ? savedCoupon.getTotalUses() : 3;

        if (userIds != null && !userIds.isEmpty()) {
            // Issue riêng lẻ
            List<com.example.gympool.entity.Member> members = memberRepository.findAllById(userIds);
            for (com.example.gympool.entity.Member member : members) {
                IssuedCoupon issued = new IssuedCoupon();
                issued.setCoupon(savedCoupon);
                issued.setMember(member);
                issued.setRemainingUses(totalUses);
                issued.setStatus("AVAILABLE");
                issuedCouponRepository.save(issued);
            }

        } else if (couponRequest.getScope() != null && !couponRequest.getScope().isEmpty()) {
            // Issue theo Scope (Membership Tier)
            // 2. Tìm tất cả CustomerMembership có tier.name = coupon.scope
            List<CustomerMembership> matchedMemberships =
                    customerMembershipRepository.findByMembershipPlan_MembershipTier_Name(couponRequest.getScope());

            // 3. Với mỗi member, tạo IssuedCoupon
            for (CustomerMembership cm : matchedMemberships) {
                IssuedCoupon issued = new IssuedCoupon();
                issued.setCoupon(savedCoupon);
                issued.setMember(cm.getMember());
                issued.setRemainingUses(totalUses); 
                issued.setStatus("AVAILABLE");
                issuedCouponRepository.save(issued);
            }
        }
    }

    @Transactional
    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {

        return couponRepository.findById(id).map(coupon -> {

            boolean scopeChanged = !coupon.getScope().equals(updatedCoupon.getScope());

            // Cập nhật các field
            coupon.setCode(updatedCoupon.getCode());
            coupon.setDiscountType(updatedCoupon.getDiscountType());
            coupon.setDiscountValue(updatedCoupon.getDiscountValue());
            coupon.setStartDate(updatedCoupon.getStartDate());
            coupon.setEndDate(updatedCoupon.getEndDate());
            coupon.setStatus(updatedCoupon.getStatus());
            coupon.setScope(updatedCoupon.getScope());

            Coupon saved = couponRepository.save(coupon);

            // =========================
            //  Nếu scope thay đổi
            // =========================
            if (scopeChanged) {

                // 1. Xóa toàn bộ IssuedCoupon trước đó
                issuedCouponRepository.deleteByCoupon(saved);

                // 2. Tìm các CustomerMembership phù hợp scope mới
                List<CustomerMembership> matchedMemberships =
                        customerMembershipRepository.findByMembershipPlan_MembershipTier_Name(
                                updatedCoupon.getScope()
                        );

                // 3. Tạo lại IssuedCoupon mới
                for (CustomerMembership cm : matchedMemberships) {
                    IssuedCoupon issued = new IssuedCoupon();
                    issued.setCoupon(saved);
                    issued.setMember(cm.getMember());
                    issued.setRemainingUses(3);  // hoặc dynamic
                    issued.setStatus("AVAILABLE");
                    issuedCouponRepository.save(issued);
                }
            }

            return saved;

        }).orElseThrow(() ->
                new RuntimeException("Không tìm thấy coupon với id " + id)
        );
    }


    @Override
    public void deleteCoupon(Long id) {
        // First, delete all issued coupons related to this coupon
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        // Delete all issued coupons for this coupon
        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findByCoupon(coupon);
        issuedCouponRepository.deleteAll(issuedCoupons);
        
        // Then delete the coupon itself
        couponRepository.deleteById(id);
    }

    @Override
    public List<IssuedCoupon> getIssuedCouponsByCouponId(Coupon coupon) {
        return issuedCouponRepository.findByCoupon(coupon);
    }
}
