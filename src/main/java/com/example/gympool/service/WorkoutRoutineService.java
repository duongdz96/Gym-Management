package com.example.gympool.service;

import com.example.gympool.entity.WorkoutRoutine;
import java.util.List;

public interface WorkoutRoutineService {
    List<WorkoutRoutine> getAllRoutines();
    WorkoutRoutine getRoutineById(Long id);
    List<WorkoutRoutine> searchByName(String name);
    List<WorkoutRoutine> findByMuscleGroup(String muscleGroup);
    WorkoutRoutine createRoutine(WorkoutRoutine routine);
    WorkoutRoutine updateRoutine(Long id, WorkoutRoutine routineDetails);
    void deleteRoutine(Long id);

    List<WorkoutRoutine> getAllVisibleRoutines(Long currentUserId);
    List<WorkoutRoutine> searchRoutines(String name, Long currentUserId);
}