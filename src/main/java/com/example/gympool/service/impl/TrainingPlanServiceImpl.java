package com.example.gympool.service.impl;

import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.TrainingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainingPlanServiceImpl implements TrainingPlanService {

    @Autowired
    private TrainingPlansRepository trainingPlanRepository;
    @Autowired
    private WorkoutRoutineRepository routineRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public TrainingPlans createPlanFromRoutine(Long routineId, Long memberId, LocalDate date) {
        WorkoutRoutine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu WorkoutRoutine id: " + routineId));

        // 2. Tìm Member (Người tập)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Member id: " + memberId));

        // 3. Tạo Instance mới cho TrainingPlans
        TrainingPlans plan = new TrainingPlans();
        plan.setMember(member);
        plan.setDate(date != null ? date : LocalDate.now());
        plan.setMuscleGroupFocus(routine.getMuscleGroupFocus());
        plan.setStatus("PENDING");
        plan.setNotes("Lịch tập tạo từ mẫu: " + routine.getName());

        List<TrainingPlanDetails> details = new ArrayList<>();
        if (routine.getRoutineDetails() != null) {
            for (RoutineDetail rd : routine.getRoutineDetails()) {
                TrainingPlanDetails tpd = new TrainingPlanDetails();
                tpd.setExercise(rd.getExercise());
                tpd.setSetCount(rd.getDefaultSetCount());
                tpd.setRepCount(rd.getDefaultRepCount());
                tpd.setStatus("PENDING");
                tpd.setTrainingPlan(plan);
                details.add(tpd);
            }
        }
        plan.setDetails(details);

        return trainingPlanRepository.save(plan);
    }

    @Override
    public TrainingPlans savePlan(TrainingPlans plan) {
        if (plan.getDetails() != null) {
            plan.getDetails().forEach(d -> d.setTrainingPlan(plan));
        }
        return trainingPlanRepository.save(plan);
    }

    @Override
    public List<TrainingPlans> getMemberHistory(Long memberId) {
        return trainingPlanRepository.findByMemberId(memberId);
    }

    @Override
    public TrainingPlans getTodayPlan(Long memberId) {
        return trainingPlanRepository.findByMemberIdAndDate(memberId, LocalDate.now())
                .orElse(null);
    }

    @Override
    @Transactional
    public TrainingPlans updateStatus(Long planId, String status) {
        TrainingPlans plan = trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Plan"));
        plan.setStatus(status);
        return trainingPlanRepository.save(plan);
    }

    @Override
    public TrainingPlans findById(Long planId) {
        return trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Training Plan với ID: " + planId));
    }
}