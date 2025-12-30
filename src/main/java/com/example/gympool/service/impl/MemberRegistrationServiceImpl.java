package com.example.gympool.service.impl;

import com.example.gympool.entity.ClassSchedule;
import com.example.gympool.entity.FitnessClass;
import com.example.gympool.entity.Member;
import com.example.gympool.entity.MemberRegistration;
import com.example.gympool.repository.ClassScheduleRepository;
import com.example.gympool.repository.MemberRegistrationRepository;
import com.example.gympool.repository.MemberRepository;
import com.example.gympool.service.MemberRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemberRegistrationServiceImpl implements MemberRegistrationService {

    @Autowired
    private MemberRegistrationRepository memberRegistrationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Override
    public List<MemberRegistration> getByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với id: " + memberId));
        return memberRegistrationRepository.findByMember(member);
    }

    @Override
    public List<MemberRegistration> getByClassSchedule(Long scheduleId) {
        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch học với id: " + scheduleId));
        // Lưu ý: Cần có phương thức findByClassSchedule trong Repository
        return memberRegistrationRepository.findByClassSchedule(schedule);
    }

    @Override
    @Transactional
    public MemberRegistration registerForClass(Long memberId, Long scheduleId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với id: " + memberId));

        ClassSchedule schedule = classScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch học với id: " + scheduleId));

        int capacity = (schedule.getCapacity() != null) ? schedule.getCapacity() : 1;

        boolean alreadyRegistered = memberRegistrationRepository.findByMemberAndClassSchedule(member, schedule).isPresent();
        if (alreadyRegistered) {
            throw new RuntimeException("Học viên đã đăng ký lớp học này rồi.");
        }

        // Check for schedule conflicts
        List<MemberRegistration> memberRegistrations = memberRegistrationRepository.findByMember(member);
        for (MemberRegistration existingReg : memberRegistrations) {
            ClassSchedule existingSchedule = existingReg.getClassSchedule();
            if (schedulesOverlap(schedule, existingSchedule)) {
                throw new RuntimeException(
                    "Lịch học bị trùng với lớp " + 
                    (existingSchedule.getFitnessClass() != null ? existingSchedule.getFitnessClass().getName() : "khác") +
                    " vào " + schedule.getStartTime().toLocalDate()
                );
            }
        }

        //cái này để tăng, về cơ bản là select count(*)
        int currentRegistrations = memberRegistrationRepository.countByClassScheduleId(scheduleId);

        if (currentRegistrations >= capacity) {
            throw new RuntimeException("Lớp học đã đầy! Sức chứa tối đa: " + capacity);
        }

        MemberRegistration newRegistration = new MemberRegistration();
        newRegistration.setClassSchedule(schedule);
        newRegistration.setMember(member);
        newRegistration.setFollowDate(Date.from(Instant.now()));

        return memberRegistrationRepository.save(newRegistration);
    }
    
    private boolean schedulesOverlap(ClassSchedule sch1, ClassSchedule sch2) {
        // Check if dates are the same
        if (!sch1.getStartTime().toLocalDate().equals(sch2.getStartTime().toLocalDate())) {
            return false;
        }
        
        // Check if times overlap
        LocalDateTime start1 = sch1.getStartTime();
        LocalDateTime end1 = sch1.getEndTime();
        LocalDateTime start2 = sch2.getStartTime();
        LocalDateTime end2 = sch2.getEndTime();
        
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    @Override
    @Transactional
    public void cancelRegistration(Long registrationId, Long memberId) {
        MemberRegistration registration = memberRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi đăng ký với id: " + registrationId));

        if (!registration.getMember().getId().equals(memberId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch học của người khác!");
        }

        // Get the fitness class of this registration
        FitnessClass fitnessClass = registration.getClassSchedule().getFitnessClass();
        if (fitnessClass == null) {
            // If no fitness class, just delete this single registration
            memberRegistrationRepository.delete(registration);
            return;
        }

        // Find and delete ALL registrations of this member for this fitness class
        Member member = registration.getMember();
        List<MemberRegistration> allRegistrations = memberRegistrationRepository.findByMember(member);

        for (MemberRegistration reg : allRegistrations) {
            if (reg.getClassSchedule() != null &&
                reg.getClassSchedule().getFitnessClass() != null &&
                reg.getClassSchedule().getFitnessClass().getId().equals(fitnessClass.getId())) {
                memberRegistrationRepository.delete(reg);
            }
        }
    }

    @Override
    public List<MemberRegistration> registerBulk(Long memberId, List<Long> scheduleIds) {
        List<MemberRegistration> resultList = new ArrayList<>();
        for(Long scheduleId : scheduleIds){
            MemberRegistration reg = this.registerForClass(memberId, scheduleId);
            resultList.add(reg);
        }
        return resultList;
    }

    @Override
    @Transactional
    public void cancelBulkRegistration(List<Long> registrationIds, Long memberId) {
        for (Long regId : registrationIds) {
            this.cancelRegistration(regId, memberId);
        }
    }
}