package com.example.gympool.service;

import com.example.gympool.entity.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService {
    Teacher createTeacher(Teacher teacher);
    Teacher updateTeacher(Long id, Teacher teacher);
    void deleteTeacher(Long id);
    Teacher getTeacherById(Long id);
    Optional<Teacher> getTeacherByEmail(String email);
    List<Teacher> getAllTeachers();
}
