package com.example.gympool.service.impl;

import com.example.gympool.entity.Exercise;
import com.example.gympool.entity.RoutineDetail;
import com.example.gympool.entity.WorkoutRoutine;
import com.example.gympool.repository.ExerciseRepository;
import com.example.gympool.repository.WorkoutRoutineRepository;
import com.example.gympool.service.WorkoutRoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class WorkoutRoutineServiceImpl implements WorkoutRoutineService {

    @Autowired
    private WorkoutRoutineRepository routineRepository;
    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public List<WorkoutRoutine> getAllRoutines() {
        return routineRepository.findAll();
    }

    @Override
    public WorkoutRoutine getRoutineById(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Routine với id: " + id));
    }

    @Override
    public List<WorkoutRoutine> searchByName(String name) {
        return routineRepository.findByNameContaining(name);
    }

    @Override
    public List<WorkoutRoutine> findByMuscleGroup(String muscleGroup) {
        return routineRepository.findByMuscleGroupFocus(muscleGroup);
    }

    @Override
    @Transactional
    public WorkoutRoutine createRoutine(WorkoutRoutine routine) {
        if (routine.getRoutineDetails() != null) {
            for (RoutineDetail detail : routine.getRoutineDetails()) {
                if (detail.getExercise() == null || detail.getExercise().getId() == null) {
                    throw new RuntimeException("Mỗi dòng chi tiết phải có Exercise ID.");
                }

                Long exerciseId = detail.getExercise().getId();
                Exercise ex = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy Exercise với ID: " + exerciseId));

                detail.setExercise(ex);

                detail.setRoutine(routine);
            }
        }
        return routineRepository.save(routine);
    }

    @Override
    @Transactional
    public WorkoutRoutine updateRoutine(Long id, WorkoutRoutine details) {
        WorkoutRoutine existingRoutine = getRoutineById(id);

        existingRoutine.setName(details.getName());
        existingRoutine.setDescription(details.getDescription());
        existingRoutine.setMuscleGroupFocus(details.getMuscleGroupFocus());
        existingRoutine.setPublic(details.isPublic());

        if (details.getRoutineDetails() != null) {
            existingRoutine.getRoutineDetails().clear();
            details.getRoutineDetails().forEach(detail -> {
                detail.setRoutine(existingRoutine);
                existingRoutine.getRoutineDetails().add(detail);
            });
        }

        return routineRepository.save(existingRoutine);
    }

    @Override
    public void deleteRoutine(Long id) {
        WorkoutRoutine routine = getRoutineById(id);
        routineRepository.delete(routine);
    }

    @Override
    public List<WorkoutRoutine> getAllVisibleRoutines(Long currentUserId) {
        List<WorkoutRoutine> routines = routineRepository.findAllVisibleRoutines(currentUserId);

        return routines.stream()
                .sorted((r1, r2) -> {
                    boolean isMine1 = r1.getCreator().getId().equals(currentUserId);
                    boolean isMine2 = r2.getCreator().getId().equals(currentUserId);
                    return Boolean.compare(isMine2, isMine1); // True (mine) lên trước
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkoutRoutine> searchRoutines(String name, Long currentUserId) {
        return routineRepository.searchByNameVisible(name, currentUserId);
    }
}