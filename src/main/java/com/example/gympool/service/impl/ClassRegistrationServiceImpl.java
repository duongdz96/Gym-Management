package com.example.gympool.service.impl;

import com.example.gympool.entity.ClassRegistration;
import com.example.gympool.entity.FitnessClass;
import com.example.gympool.entity.Teacher;
import com.example.gympool.repository.ClassRegistrationRepository;
import com.example.gympool.repository.FitnessClassRepository;
import com.example.gympool.repository.TeacherRepository;
import com.example.gympool.service.ClassRegistrationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRegistrationServiceImpl implements ClassRegistrationService {

    @Autowired
    private ClassRegistrationRepository classRegistrationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Override
    public List<ClassRegistration> getByTeacher(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + teacherId));
        return classRegistrationRepository.findByTeacher(teacher);
    }

    @Override
    public List<ClassRegistration> getByFitnessClass(Long templateId) {
        FitnessClass template = fitnessClassRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("ClassTemplate not found with id: " + templateId));
        return classRegistrationRepository.findByFitnessClass(template);
    }

    @Override
    public ClassRegistration registerTeaching(ClassRegistration reg) {
        Long teacherId = reg.getTeacher().getId();

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + teacherId));
        FitnessClass template = fitnessClassRepository.findById(reg.getFitnessClass().getId())
                .orElseThrow(() -> new RuntimeException("ClassTemplate not found"));

        reg.setTeacher(teacher);
        reg.setFitnessClass(template);

        return classRegistrationRepository.save(reg);
    }

    @Override
    public void unregisterTeachingById(Long staffId, Long registrationId) {
        ClassRegistration registration = classRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id: " + registrationId));
        if (!registration.getTeacher().getId().equals(staffId)) {
            throw new AccessDeniedException("You cannot unregister a class registered by another person.");
        }
        classRegistrationRepository.deleteById(registrationId);
    }
}
