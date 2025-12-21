package com.example.gympool.repository;

import com.example.gympool.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise,Long> {
    Optional<Exercise> findByName(String name);

    List<Exercise> findByCreatorId(Long userId);

    @Query("SELECT e FROM Exercise e WHERE e.creator.id = :userId OR e.creator IS NULL")
    List<Exercise> findAllPersonalAndSystemExercises(@Param("userId") Long userId);
}
