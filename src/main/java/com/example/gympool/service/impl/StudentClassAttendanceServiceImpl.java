package com.example.gympool.service.impl;

import com.example.gympool.repository.*;
import com.example.gympool.service.StudentClassAttendanceService;
import com.example.gympool.entity.*;
import com.example.gympool.dto.AttendanceCheckInRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentClassAttendanceServiceImpl implements StudentClassAttendanceService {

    @Autowired
    private StudentClassAttendanceRepository attendanceRepository;
    @Autowired
    private MemberRegistrationRepository registrationRepository;
    @Autowired
    private ClassScheduleRepository  classScheduleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeacherRepository teacherRepository;


    @Override
    @Transactional
    public StudentClassAttendance checkInStudent(AttendanceCheckInRequest request) {
        ClassSchedule classSchedule = classScheduleRepository.findById(request.getClassScheduleId())
                .orElseThrow(() -> new RuntimeException("Class Schedule not found"));
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Teacher checker = teacherRepository.findById(request.getCheckedInById())
                .orElseThrow(() -> new RuntimeException("Teacher/Checker not found"));

        if (registrationRepository.findByMemberAndClassSchedule(member, classSchedule).isEmpty()) {
            throw new RuntimeException("Member is not registered for this class schedule.");
        }

        Optional<StudentClassAttendance> existingAttendanceOpt = attendanceRepository.findByClassScheduleIdAndMemberId(classSchedule.getId(), member.getId());

        if (existingAttendanceOpt.isPresent()) {
            StudentClassAttendance existingAttendance = existingAttendanceOpt.get();

            if ("PRESENT".equals(existingAttendance.getStatus())) {
                throw new RuntimeException("Attendance record already marked PRESENT.");
            } else if ("ABSENT".equals(existingAttendance.getStatus())) {
                existingAttendance.setStatus("PRESENT");
                existingAttendance.setCheckedInAt(LocalDateTime.now());
                existingAttendance.setCheckedInBy(checker);
                existingAttendance.setNotes(null);
                return attendanceRepository.save(existingAttendance);
            }
        }

        StudentClassAttendance attendance = new StudentClassAttendance();
        attendance.setClassSchedule(classSchedule);
        attendance.setMember(member);
        attendance.setStatus("PRESENT");
        attendance.setCheckedInAt(LocalDateTime.now());
        attendance.setCheckedInBy(checker);

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<StudentClassAttendance> getAttendanceByClassSchedule(Long classScheduleId) {
        return attendanceRepository.findAll().stream()
                .filter(a -> a.getClassSchedule().getId().equals(classScheduleId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int processAutoAbsent(Long classScheduleId) {
        ClassSchedule classSchedule = classScheduleRepository.findById(classScheduleId)
                .orElseThrow(() -> new RuntimeException("Class Schedule not found"));

        List<MemberRegistration> registeredMembers = registrationRepository.findByClassSchedule(classSchedule);

        List<StudentClassAttendance> existingAttendance = attendanceRepository.findAllByClassScheduleId(classScheduleId); // Giả định phương thức này tồn tại

        List<Long> attendedMemberIds = existingAttendance.stream()
                .map(a -> a.getMember().getId())
                .collect(Collectors.toList());

        List<MemberRegistration> absentRegistrations = registeredMembers.stream()
                .filter(reg -> !attendedMemberIds.contains(reg.getMember().getId()))
                .collect(Collectors.toList());

        int absentCount = 0;
        for (MemberRegistration reg : absentRegistrations) {
            Optional<StudentClassAttendance> existing = attendanceRepository.findByClassScheduleIdAndMemberId(classScheduleId, reg.getMember().getId());
            if (existing.isEmpty()) {
                StudentClassAttendance attendance = new StudentClassAttendance();
                attendance.setClassSchedule(classSchedule);
                attendance.setMember(reg.getMember());
                attendance.setStatus("ABSENT");
                attendance.setNotes("Auto Absent (Class Ended)");
                // checkedInAt và checkedInBy là null

                attendanceRepository.save(attendance);
                absentCount++;
            }
        }

        return absentCount;
    }


    @Override
    @Transactional
    public StudentClassAttendance updateAttendanceStatus(Long classScheduleId, Long memberId, Long teacherId, String newStatus) {
        StudentClassAttendance existingAttendance = attendanceRepository
                .findByClassScheduleIdAndMemberId(classScheduleId, memberId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found for this member and class."));

        Teacher updater = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher/Updater not found"));

        switch (newStatus.toUpperCase()) {
            case "ABSENT":
                existingAttendance.setStatus("ABSENT");
                existingAttendance.setCheckedInAt(null); // Hủy thời gian check-in nếu có
                existingAttendance.setNotes("Manually marked ABSENT.");
                break;
            case "PRESENT":
                existingAttendance.setStatus("PRESENT");
                if (existingAttendance.getCheckedInAt() == null) {
                    existingAttendance.setCheckedInAt(LocalDateTime.now());
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid attendance status: " + newStatus);
        }
        return attendanceRepository.save(existingAttendance);
    }
}