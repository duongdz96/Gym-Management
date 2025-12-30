package com.example.gympool.service.impl;

import com.example.gympool.entity.ClassRegistration;
import com.example.gympool.entity.ClassSchedule;
import com.example.gympool.entity.FitnessClass;
import com.example.gympool.entity.Teacher;
import com.example.gympool.repository.ClassRegistrationRepository;
import com.example.gympool.repository.ClassScheduleRepository;
import com.example.gympool.repository.FitnessClassRepository;
import com.example.gympool.repository.TeacherRepository;
import com.example.gympool.service.ClassRegistrationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClassRegistrationServiceImpl implements ClassRegistrationService {

    @Autowired
    private ClassRegistrationRepository classRegistrationRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FitnessClassRepository fitnessClassRepository;

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

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
        reg.setStatus("PENDING"); // Set default status

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

    @Override
    public ClassRegistration approveRegistration(Long registrationId) {
        ClassRegistration registration = classRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id: " + registrationId));
        
        // Check for schedule conflicts before approving
        Teacher teacher = registration.getTeacher();
        FitnessClass newClass = registration.getFitnessClass();
        
        // Get all schedules for the new class
        List<ClassSchedule> newSchedules = classScheduleRepository.findByFitnessClassId(newClass.getId());
        
        // Get all APPROVED registrations for this teacher
        List<ClassRegistration> approvedRegistrations = classRegistrationRepository.findByTeacher(teacher)
                .stream()
                .filter(r -> "APPROVED".equals(r.getStatus()) && !r.getId().equals(registrationId))
                .toList();
        
        // Check for conflicts with existing approved classes
        for (ClassRegistration approvedReg : approvedRegistrations) {
            List<ClassSchedule> existingSchedules = classScheduleRepository.findByFitnessClassId(approvedReg.getFitnessClass().getId());
            
            for (ClassSchedule newSch : newSchedules) {
                for (ClassSchedule existingSch : existingSchedules) {
                    // Check if schedules overlap
                    if (schedulesOverlap(newSch, existingSch)) {
                        throw new IllegalStateException(
                            "Giáo viên có lịch trùng với lớp " + approvedReg.getFitnessClass().getName() +
                            " vào ngày " + newSch.getStartTime().toLocalDate()
                        );
                    }
                }
            }
        }
        
        registration.setStatus("APPROVED");
        return classRegistrationRepository.save(registration);
    }
    
    private boolean schedulesOverlap(ClassSchedule sch1, ClassSchedule sch2) {
        // Check if dates are the same
        if (!sch1.getStartTime().toLocalDate().equals(sch2.getStartTime().toLocalDate())) {
            return false;
        }
        
        // Check if times overlap
        LocalDateTime start1 = sch1.getStartTime();
        LocalDateTime end1 = sch1.getEndTime();
        LocalDateTime start2 = sch2.getStartTime();
        LocalDateTime end2 = sch2.getEndTime();
        
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    @Override
    public ClassRegistration rejectRegistration(Long registrationId, String reason) {
        ClassRegistration registration = classRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new EntityNotFoundException("Registration not found with id: " + registrationId));
        
        registration.setStatus("REJECTED");
        if (reason != null && !reason.isEmpty()) {
            registration.setDescription(reason); // Store rejection reason in description
        }
        return classRegistrationRepository.save(registration);
    }
}
