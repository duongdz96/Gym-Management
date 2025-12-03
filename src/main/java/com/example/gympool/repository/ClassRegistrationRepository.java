package com.example.gympool.repository;

import com.example.gympool.entity.ClassRegistration;
import com.example.gympool.entity.FitnessClass;
import com.example.gympool.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRegistrationRepository extends JpaRepository<ClassRegistration, Long> {
    List<ClassRegistration> findByTeacher(Teacher teacher);
    List<ClassRegistration> findByFitnessClass(FitnessClass fitnessClass);
}
