package com.example.gympool.repository;

import com.example.gympool.entity.WorkoutRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkoutRoutineRepository extends JpaRepository<WorkoutRoutine,Long> {
    List<WorkoutRoutine> findByNameContaining(String name);
    List<WorkoutRoutine> findByMuscleGroupFocus(String musclegroupfocus);

    @Query("SELECT w FROM WorkoutRoutine w WHERE w.creator.id = :userId OR w.isPublic = true")
    List<WorkoutRoutine> findAllVisibleRoutines(@Param("userId") Long userId);

    @Query("SELECT w FROM WorkoutRoutine w WHERE (w.name LIKE %:name%) " +
            "AND (w.creator.id = :userId OR w.isPublic = true)")
    List<WorkoutRoutine> searchByNameVisible(@Param("name") String name, @Param("userId") Long userId);
}
