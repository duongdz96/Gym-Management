package com.example.gympool.controller;

import com.example.gympool.entity.FitnessClass;
import com.example.gympool.service.FitnessClassService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fitness_class")
public class FitnessClassController {

    private final FitnessClassService fitnessClassService;

    public FitnessClassController(FitnessClassService classTypeService) {
        this.fitnessClassService = classTypeService;
    }

    @GetMapping
    public ResponseEntity<List<FitnessClass>> getAllFitnessClass() {
        return ResponseEntity.ok(fitnessClassService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FitnessClass> getById(@PathVariable Long id) {
        return fitnessClassService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FitnessClass> CreateFitnessClass(@RequestBody FitnessClass fitnessClass) {
        return ResponseEntity.ok(fitnessClassService.create(fitnessClass));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FitnessClass> update(@PathVariable Long id, @RequestBody FitnessClass fitnessClass) {
        return ResponseEntity.ok(fitnessClassService.update(id, fitnessClass));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fitnessClassService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    /api/fitness_classes/paged?status=open&page=0&size=10
    @GetMapping("/paged")
    public ResponseEntity<?> getClasses(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<FitnessClass> result = fitnessClassService.getClassesForManager(status, pageable);

        return ResponseEntity.ok(result);
    }

//    /api/fitness_class/status/1?status=open
    @PutMapping("/status/{id}")
    public ResponseEntity<FitnessClass> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(fitnessClassService.updateStatus(id, status));
    }
}
