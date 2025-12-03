package com.example.gympool.controller;

import com.example.gympool.entity.ClassRegistration;
import com.example.gympool.service.ClassRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-registrations")
public class ClassRegistrationController {

    private final ClassRegistrationService classRegistrationService;

    public ClassRegistrationController(ClassRegistrationService classRegistrationService) {
        this.classRegistrationService = classRegistrationService;
    }

    // Lấy danh sách lớp mà staff đã đăng ký dạy
    @GetMapping("/teacher/{staffId}")
    public ResponseEntity<List<ClassRegistration>> getByTeacher(@PathVariable Long staffId) {
        return ResponseEntity.ok(classRegistrationService.getByTeacher(staffId));
    }

    // Lấy danh sách giáo viên đã đăng ký cho class template
    @GetMapping("/fitness_class/{templateId}")
    public ResponseEntity<List<ClassRegistration>> getByFitnessClass(@PathVariable Long templateId) {
        return ResponseEntity.ok(classRegistrationService.getByFitnessClass(templateId));
    }

    // Staff đăng ký dạy 1 lớp
    @PostMapping
    public ResponseEntity<ClassRegistration> registerTeaching(@RequestBody Long staffId, ClassRegistration reg) {
        return ResponseEntity.ok(classRegistrationService.registerTeaching(staffId,reg));
    }

    // Staff hủy đăng ký dạy
    @DeleteMapping("/{id}")
    public ResponseEntity<String> unregisterTeaching(@PathVariable Long staffId ,Long registrationId) { // Dùng @PathVariable
        classRegistrationService.unregisterTeachingById(staffId, registrationId); // Gọi một service mới theo ID
        return ResponseEntity.ok("Teaching registration removed successfully");
    }
}
