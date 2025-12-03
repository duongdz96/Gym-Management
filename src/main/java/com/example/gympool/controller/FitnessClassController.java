package com.example.gympool.controller;

import com.example.gympool.entity.FitnessClass;
import com.example.gympool.service.FitnessClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fitness_class")
public class FitnessClassController {

    private final FitnessClassService classTypeService;

    public FitnessClassController(FitnessClassService classTypeService) {
        this.classTypeService = classTypeService;
    }

    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAll() {
        return ResponseEntity.ok(classTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getById(@PathVariable Long id) {
        return classTypeService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FitnessClass> create(@RequestBody FitnessClass fitnessClass) {
        return ResponseEntity.ok(classTypeService.create(fitnessClass));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessClass> update(@PathVariable Long id, @RequestBody FitnessClass fitnessClass) {
        return ResponseEntity.ok(classTypeService.update(id, fitnessClass));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        classTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
