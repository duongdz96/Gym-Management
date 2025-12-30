package com.example.gympool.service;

import com.example.gympool.entity.MemberRegistration;
import java.util.List;

public interface MemberRegistrationService {
    List<MemberRegistration> getByMember(Long memberId);
    List<MemberRegistration> getByClassSchedule(Long scheduleId);
    MemberRegistration registerForClass(Long memberId, Long scheduleId);
    void cancelRegistration(Long registrationId, Long memberId);

    List<MemberRegistration> registerBulk(Long memberId, List<Long> scheduleIds);
    void cancelBulkRegistration(List<Long> registrationIds, Long memberId);

}
