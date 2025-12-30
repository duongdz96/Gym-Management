package com.example.gympool.controller;

import com.example.gympool.entity.WorkoutRoutine;
import com.example.gympool.service.WorkoutRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-routines")
@RequiredArgsConstructor
public class WorkoutRoutineController {

    private final WorkoutRoutineService routineService;

    @GetMapping
    public ResponseEntity<List<WorkoutRoutine>> getAllRoutines(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(routineService.getAllVisibleRoutines(userId));
        }
        return ResponseEntity.ok(routineService.getAllRoutines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutRoutine> getRoutineById(@PathVariable Long id) {
        return ResponseEntity.ok(routineService.getRoutineById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<WorkoutRoutine>> searchRoutines(
            @RequestParam String name,
            @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(routineService.searchRoutines(name, userId));
        }
        return ResponseEntity.ok(routineService.searchByName(name));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<WorkoutRoutine>> findByMuscleGroup(@RequestParam("muscle") String muscleGroup) {
        return ResponseEntity.ok(routineService.findByMuscleGroup(muscleGroup));
    }

    @PostMapping
    public ResponseEntity<WorkoutRoutine> createRoutine(@RequestBody WorkoutRoutine routine) {
        return new ResponseEntity<>(routineService.createRoutine(routine), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutRoutine> updateRoutine(
            @PathVariable Long id,
            @RequestBody WorkoutRoutine routineDetails) {
        return ResponseEntity.ok(routineService.updateRoutine(id, routineDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        routineService.deleteRoutine(id);
        return ResponseEntity.noContent().build();
    }
}