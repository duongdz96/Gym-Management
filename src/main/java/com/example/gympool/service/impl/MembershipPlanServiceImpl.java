package com.example.gympool.service.impl;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.Product;
import com.example.gympool.repository.MembershipPlanRepository;
import com.example.gympool.repository.ProductRepository;
import com.example.gympool.service.MembershipPlanService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    @Autowired
    private MembershipPlanRepository MembershipPlanRepository;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public List<MembershipPlan> getAllMembershipPlan(){
        return MembershipPlanRepository.findAll();
    }
    @Override
    public MembershipPlan getMembershipPlanById(Long id){
        return MembershipPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MembershipPlan not found with id: " + id));
    }

    @Transactional
    @Override
    public MembershipPlan addMembershipPlan(MembershipPlan membershipPlan) {
        membershipPlan.setStatus("Active");

        MembershipPlan savedPlan = MembershipPlanRepository.save(membershipPlan);

        Product product = new Product();
        product.setName(savedPlan.getName());
        product.setType("Membership");
        product.setPrice(savedPlan.getPrice());
        product.setStatus(false);

        productRepository.save(product);

        return savedPlan;
    }

    @Override
    public MembershipPlan updateMembershipPlan(Long id, MembershipPlan MembershipPlanDetails) {
        MembershipPlan existingPlan = MembershipPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MembershipPlan not found"));

        String oldName = existingPlan.getName();
        // update field từ User
        if (MembershipPlanDetails.getName() != null) existingPlan.setName(MembershipPlanDetails.getName());
        if (MembershipPlanDetails.getDuration() != null) existingPlan.setDuration(MembershipPlanDetails.getDuration());
        if (MembershipPlanDetails.getStatus() != null) existingPlan.setStatus(MembershipPlanDetails.getStatus());
        if (MembershipPlanDetails.getBenefits() != null) existingPlan.setBenefits(MembershipPlanDetails.getBenefits());
        if (MembershipPlanDetails.getPrice() != null) existingPlan.setPrice(MembershipPlanDetails.getPrice());
        MembershipPlan updatedPlan = MembershipPlanRepository.save(existingPlan);

        Optional<Product> associatedProduct = productRepository.findByName(oldName);

        if (associatedProduct.isPresent()) {
            Product product = associatedProduct.get();
            product.setName(updatedPlan.getName());
            product.setPrice(updatedPlan.getPrice());
            productRepository.save(product);
        }

        return updatedPlan;
    }

    @Override
    public MembershipPlan deleteMembershipPlan(Long id) {
        MembershipPlan membershipPlan = MembershipPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy MembershipPlan với ID: " + id));

        MembershipPlanRepository.deleteById(id);

        return membershipPlan;
    }

    @Override
    public MembershipPlan changeStatusMembershipPlan(Long id, String status) {
        MembershipPlan membershipPlan = MembershipPlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy MembershipPlan với ID: " + id));
        membershipPlan.setStatus(status);
        return MembershipPlanRepository.save(membershipPlan);
    }

    @Override
    public List<MembershipPlan> getAllActiveMembershipPlans() {
        return MembershipPlanRepository.findAllByStatus("ACTIVE");
    }
}
