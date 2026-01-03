package com.example.gympool.service;

import com.example.gympool.entity.ClassRegistration;
import java.util.List;

public interface ClassRegistrationService {
    List<ClassRegistration> getByTeacher(Long teacherId);
    List<ClassRegistration> getByFitnessClass(Long fitnessClassId);
    // Staff đăng ký dạy 1 lớp
    ClassRegistration registerTeaching(ClassRegistration reg);

    // Staff hủy đăng ký dạy
    void unregisterTeachingById(Long staffId, Long registrationId);

    // Manager approve registration
    ClassRegistration approveRegistration(Long registrationId);

    // Manager reject registration
    ClassRegistration rejectRegistration(Long registrationId, String reason);

}
