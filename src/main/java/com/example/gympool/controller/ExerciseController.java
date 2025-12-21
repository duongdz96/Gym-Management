package com.example.gympool.controller;

import com.example.gympool.entity.Exercise;
import com.example.gympool.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    // 1. Lấy toàn bộ danh sách
    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseService.getAllExercises();
    }

    // 2. Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long id) {
        return exerciseService.getExerciseById(id)
                .map(ResponseEntity::ok) // Nếu có dữ liệu trả về 200 OK
                .orElse(ResponseEntity.notFound().build()); // Nếu không thấy trả về 404
    }

    // 3. Tạo mới
    @PostMapping
    public Exercise createExercise(@RequestBody Exercise exercise) {
        return exerciseService.createExercise(exercise);
    }

    // 4. Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise exercise) {
        try {
            Exercise updatedExercise = exerciseService.updateExercise(id, exercise);
            return ResponseEntity.ok(updatedExercise);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        try {
            exerciseService.deleteExercise(id);
            return ResponseEntity.noContent().build(); // Trả về 204 No Content sau khi xóa thành công
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. Tìm kiếm theo tên
    @GetMapping("/search")
    public ResponseEntity<Exercise> getExerciseByName(@RequestParam String name) {
        return exerciseService.getExerciseByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Exercise>> getMyExercises(@PathVariable Long userId) {
        List<Exercise> exercises = exerciseService.getMyExercises(userId);
        return ResponseEntity.ok(exercises);
    }
}