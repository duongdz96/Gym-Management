package com.example.gympool.service.impl;

import com.example.gympool.entity.Exercise;
import com.example.gympool.repository.ExerciseRepository;
import com.example.gympool.repository.UserRepository;
import com.example.gympool.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @Override
    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise updateExercise(Long id, Exercise exercise) {
        Exercise existedExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Exercise với id: " + id));

        existedExercise.setName(exercise.getName());
        existedExercise.setDescription(exercise.getDescription());
        existedExercise.setMuscleGroup(exercise.getMuscleGroup());
        return exerciseRepository.save(existedExercise);
    }

    @Override
    public void deleteExercise(Long id) {
        Exercise existedExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Exercise với id: " + id));

        exerciseRepository.deleteById(id);
    }

    @Override
    public Optional<Exercise> getExerciseByName(String name) {
        return exerciseRepository.findByName(name);
    }

    @Override
    public List<Exercise> getMyExercises(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return exerciseRepository.findAllPersonalAndSystemExercises(userId);
    }
}
