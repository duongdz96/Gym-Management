package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="membership_plan")
public class MembershipPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;    //gold 1 month
    private String duration;
    private String status;
    private String benefits;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier membershipTier;
}
