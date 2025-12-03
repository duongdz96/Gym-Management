package com.example.gympool.dto;

import com.example.gympool.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMembershipRequest {
    private Member member;
    private Long membershipPlanId;
    private Date startDate;
    private Date endDate;
}
