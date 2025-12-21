package com.example.gympool.service;

import com.example.gympool.entity.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseService {
    List<Exercise> getAllExercises();
    Optional<Exercise> getExerciseById(Long id);
    Exercise createExercise(Exercise exercise);
    Exercise updateExercise(Long id, Exercise Exercise);
    void deleteExercise(Long id);
    Optional<Exercise> getExerciseByName(String name);
    List<Exercise> getMyExercises(Long userId);

}
