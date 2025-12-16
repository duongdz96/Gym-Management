package com.example.gympool.service;

import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.ClassRegistrationServiceImpl;
import com.example.gympool.service.TestDataHelper;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClassRegistrationServiceTest {

    @InjectMocks
    private ClassRegistrationServiceImpl classRegistrationService;

    @Mock private ClassRegistrationRepository classRegistrationRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private FitnessClassRepository fitnessClassRepository;
    @Mock private ClassScheduleRepository classScheduleRepository;

    private Teacher mockTeacher1;
    private Teacher mockTeacher2;
    private FitnessClass mockFitnessClass1;
    private FitnessClass mockFitnessClass2;
    private ClassRegistration mockRegistration;
    private ClassSchedule mockSchedule1;
    private ClassSchedule mockSchedule2;

    @BeforeEach
    void setUp() {
        mockTeacher1 = TestDataHelper.createTeacher("John Doe", "john@gym.com", "0901234567");
        mockTeacher1.setId(1L);

        mockTeacher2 = TestDataHelper.createTeacher("Jane Smith", "jane@gym.com", "0907654321");
        mockTeacher2.setId(2L);

        mockFitnessClass1 = TestDataHelper.createFitnessClass("Yoga Basics", "Beginner", "Basic yoga class for beginners");
        mockFitnessClass1.setId(1L);

        mockFitnessClass2 = TestDataHelper.createFitnessClass("Advanced Cardio", "Advanced", "High intensity cardio workout");
        mockFitnessClass2.setId(2L);

        mockRegistration = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass1,
                "PENDING",
                "I would like to teach this class"
        );
        mockRegistration.setId(100L);

        // Schedules for testing overlap
        mockSchedule1 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0),
                mockFitnessClass1
        );
        mockSchedule1.setId(1L);

        mockSchedule2 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                LocalDateTime.of(2024, 1, 15, 11, 30),
                mockFitnessClass2
        );
        mockSchedule2.setId(2L);
    }

    // ==================== REGISTER TEACHING TESTS ====================

    @Test
    void registerTeaching_Success_ShouldSetPendingStatus() {
        ClassRegistration inputReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass1,
                null, // Status not set yet
                "Test description"
        );

        when(teacherRepository.findById(mockTeacher1.getId())).thenReturn(Optional.of(mockTeacher1));
        when(fitnessClassRepository.findById(mockFitnessClass1.getId())).thenReturn(Optional.of(mockFitnessClass1));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> {
            ClassRegistration reg = invocation.getArgument(0);
            reg.setId(100L);
            return reg;
        });

        ClassRegistration result = classRegistrationService.registerTeaching(inputReg);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTeacher()).isEqualTo(mockTeacher1);
        assertThat(result.getFitnessClass()).isEqualTo(mockFitnessClass1);
        verify(classRegistrationRepository, times(1)).save(any(ClassRegistration.class));
    }

    @Test
    void registerTeaching_WithDescription_ShouldSaveDescription() {
        ClassRegistration inputReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass1,
                null,
                "I have 5 years of experience"
        );

        when(teacherRepository.findById(mockTeacher1.getId())).thenReturn(Optional.of(mockTeacher1));
        when(fitnessClassRepository.findById(mockFitnessClass1.getId())).thenReturn(Optional.of(mockFitnessClass1));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.registerTeaching(inputReg);

        assertThat(result.getDescription()).isEqualTo("I have 5 years of experience");
    }

    @Test
    void registerTeaching_TeacherNotFound_ShouldThrowException() {
        ClassRegistration inputReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass1,
                null,
                "Test"
        );

        when(teacherRepository.findById(mockTeacher1.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRegistrationService.registerTeaching(inputReg);
        });

        verify(classRegistrationRepository, never()).save(any());
    }

    @Test
    void registerTeaching_FitnessClassNotFound_ShouldThrowException() {
        ClassRegistration inputReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass1,
                null,
                "Test"
        );

        when(teacherRepository.findById(mockTeacher1.getId())).thenReturn(Optional.of(mockTeacher1));
        when(fitnessClassRepository.findById(mockFitnessClass1.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRegistrationService.registerTeaching(inputReg);
        });

        verify(classRegistrationRepository, never()).save(any());
    }

    // ==================== APPROVE REGISTRATION TESTS ====================

    @Test
    void approveRegistration_Success_ShouldSetApprovedStatus() {
        mockRegistration.setStatus("PENDING");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of()); // No other approved registrations
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        verify(classRegistrationRepository, times(1)).save(mockRegistration);
    }

    @Test
    void approveRegistration_NoConflict_ShouldApprove() {
        mockRegistration.setStatus("PENDING");

        // Create another approved registration with different schedule (no overlap)
        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );
        approvedReg.setId(200L);

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1)); // 9:00-10:00
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(mockSchedule2)); // 10:30-11:30 (no overlap)
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void approveRegistration_WithScheduleConflict_ShouldThrowException() {
        mockRegistration.setStatus("PENDING");

        // Create overlapping schedule
        ClassSchedule overlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 30), // Overlaps with mockSchedule1 (9:00-10:00)
                LocalDateTime.of(2024, 1, 15, 10, 30),
                mockFitnessClass2
        );

        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );
        approvedReg.setId(200L);

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(overlappingSchedule));

        assertThrows(IllegalStateException.class, () -> {
            classRegistrationService.approveRegistration(100L);
        });

        verify(classRegistrationRepository, never()).save(any());
    }

    @Test
    void approveRegistration_SameDateOverlappingTime_ShouldThrowException() {
        mockRegistration.setStatus("PENDING");

        // Same date, overlapping time: 9:00-10:00 vs 9:30-10:30
        ClassSchedule overlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 30),
                LocalDateTime.of(2024, 1, 15, 10, 30),
                mockFitnessClass2
        );

        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(overlappingSchedule));

        assertThrows(IllegalStateException.class, () -> {
            classRegistrationService.approveRegistration(100L);
        });
    }

    @Test
    void approveRegistration_SameDateNonOverlapping_ShouldApprove() {
        mockRegistration.setStatus("PENDING");

        // Same date, but non-overlapping: 9:00-10:00 vs 10:00-11:00 (edge case)
        ClassSchedule nonOverlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 10, 0), // Starts exactly when mockSchedule1 ends
                LocalDateTime.of(2024, 1, 15, 11, 0),
                mockFitnessClass2
        );

        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(nonOverlappingSchedule));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void approveRegistration_DifferentDate_ShouldApprove() {
        mockRegistration.setStatus("PENDING");

        // Different date - no conflict
        ClassSchedule differentDateSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 16, 9, 0), // Different day
                LocalDateTime.of(2024, 1, 16, 10, 0),
                mockFitnessClass2
        );

        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(differentDateSchedule));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void approveRegistration_MultipleApprovedClasses_ShouldCheckAll() {
        mockRegistration.setStatus("PENDING");

        // Teacher has 2 approved classes already
        ClassRegistration approvedReg1 = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "APPROVED",
                null
        );
        approvedReg1.setId(200L);

        FitnessClass class3 = TestDataHelper.createFitnessClass("Pilates", "Intermediate", "Pilates class for core strength");
        class3.setId(3L);
        ClassRegistration approvedReg2 = TestDataHelper.createClassRegistration(
                mockTeacher1,
                class3,
                "APPROVED",
                null
        );
        approvedReg2.setId(300L);

        ClassSchedule schedule3 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 16, 9, 0),
                LocalDateTime.of(2024, 1, 16, 10, 0),
                class3
        );

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(approvedReg1, approvedReg2));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass2.getId()))
                .thenReturn(List.of(mockSchedule2)); // No overlap
        when(classScheduleRepository.findByFitnessClassId(class3.getId()))
                .thenReturn(List.of(schedule3)); // Different date
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        verify(classScheduleRepository, times(1)).findByFitnessClassId(mockFitnessClass2.getId());
        verify(classScheduleRepository, times(1)).findByFitnessClassId(class3.getId());
    }

    @Test
    void approveRegistration_IgnorePendingRegistrations_ShouldOnlyCheckApproved() {
        mockRegistration.setStatus("PENDING");

        // Teacher has 1 PENDING and 1 APPROVED registration
        ClassRegistration pendingReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                mockFitnessClass2,
                "PENDING", // Should be ignored
                null
        );
        pendingReg.setId(200L);

        FitnessClass class3 = TestDataHelper.createFitnessClass("Pilates", "Intermediate", "Pilates class for core strength");
        class3.setId(3L);
        ClassRegistration approvedReg = TestDataHelper.createClassRegistration(
                mockTeacher1,
                class3,
                "APPROVED",
                null
        );
        approvedReg.setId(300L);

        ClassSchedule schedule3 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 16, 9, 0),
                LocalDateTime.of(2024, 1, 16, 10, 0),
                class3
        );

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classScheduleRepository.findByFitnessClassId(mockFitnessClass1.getId()))
                .thenReturn(List.of(mockSchedule1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1))
                .thenReturn(List.of(pendingReg, approvedReg));
        when(classScheduleRepository.findByFitnessClassId(class3.getId()))
                .thenReturn(List.of(schedule3));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.approveRegistration(100L);

        assertThat(result.getStatus()).isEqualTo("APPROVED");
        // Should only check class3 (APPROVED), not class2 (PENDING)
        verify(classScheduleRepository, never()).findByFitnessClassId(mockFitnessClass2.getId());
        verify(classScheduleRepository, times(1)).findByFitnessClassId(class3.getId());
    }

    @Test
    void approveRegistration_RegistrationNotFound_ShouldThrowException() {
        when(classRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classRegistrationService.approveRegistration(999L);
        });
    }

    // ==================== UNREGISTER TEACHING TESTS ====================

    @Test
    void unregisterTeachingById_Success_ShouldDelete() {
        mockRegistration.setStatus("PENDING");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));

        classRegistrationService.unregisterTeachingById(mockTeacher1.getId(), 100L);

        verify(classRegistrationRepository, times(1)).deleteById(100L);
    }

    @Test
    void unregisterTeachingById_DifferentTeacher_ShouldThrowAccessDeniedException() {
        mockRegistration.setStatus("PENDING");
        mockRegistration.setTeacher(mockTeacher1); // Owned by teacher1

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));

        // Teacher2 tries to unregister teacher1's registration
        assertThrows(AccessDeniedException.class, () -> {
            classRegistrationService.unregisterTeachingById(mockTeacher2.getId(), 100L);
        });

        verify(classRegistrationRepository, never()).deleteById(any());
    }

    @Test
    void unregisterTeachingById_CorrectTeacher_ShouldDelete() {
        mockRegistration.setStatus("PENDING");
        mockRegistration.setTeacher(mockTeacher1);

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));

        // Teacher1 unregisters their own registration
        classRegistrationService.unregisterTeachingById(mockTeacher1.getId(), 100L);

        verify(classRegistrationRepository, times(1)).deleteById(100L);
    }

    @Test
    void unregisterTeachingById_RegistrationNotFound_ShouldThrowException() {
        when(classRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classRegistrationService.unregisterTeachingById(mockTeacher1.getId(), 999L);
        });

        verify(classRegistrationRepository, never()).deleteById(any());
    }

    // ==================== REJECT REGISTRATION TESTS ====================

    @Test
    void rejectRegistration_Success_ShouldSetRejectedStatus() {
        mockRegistration.setStatus("PENDING");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.rejectRegistration(100L, "Not qualified");

        assertThat(result.getStatus()).isEqualTo("REJECTED");
        verify(classRegistrationRepository, times(1)).save(mockRegistration);
    }

    @Test
    void rejectRegistration_WithReason_ShouldSaveReason() {
        mockRegistration.setStatus("PENDING");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.rejectRegistration(100L, "Insufficient experience");

        assertThat(result.getDescription()).isEqualTo("Insufficient experience");
    }

    @Test
    void rejectRegistration_WithoutReason_ShouldStillReject() {
        mockRegistration.setStatus("PENDING");
        mockRegistration.setDescription("Original description");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.rejectRegistration(100L, null);

        assertThat(result.getStatus()).isEqualTo("REJECTED");
        assertThat(result.getDescription()).isEqualTo("Original description"); // Unchanged
    }

    @Test
    void rejectRegistration_EmptyReason_ShouldNotSaveReason() {
        mockRegistration.setStatus("PENDING");
        mockRegistration.setDescription("Original description");

        when(classRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(classRegistrationRepository.save(any(ClassRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClassRegistration result = classRegistrationService.rejectRegistration(100L, "");

        assertThat(result.getStatus()).isEqualTo("REJECTED");
        assertThat(result.getDescription()).isEqualTo("Original description"); // Unchanged
    }

    @Test
    void rejectRegistration_RegistrationNotFound_ShouldThrowException() {
        when(classRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            classRegistrationService.rejectRegistration(999L, "Test reason");
        });
    }

    // ==================== QUERY METHODS TESTS ====================

    @Test
    void getByTeacher_ValidTeacher_ShouldReturnRegistrations() {
        List<ClassRegistration> mockRegistrations = List.of(mockRegistration);

        when(teacherRepository.findById(mockTeacher1.getId())).thenReturn(Optional.of(mockTeacher1));
        when(classRegistrationRepository.findByTeacher(mockTeacher1)).thenReturn(mockRegistrations);

        List<ClassRegistration> result = classRegistrationService.getByTeacher(mockTeacher1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockRegistration);
        verify(classRegistrationRepository, times(1)).findByTeacher(mockTeacher1);
    }

    @Test
    void getByTeacher_TeacherNotFound_ShouldThrowException() {
        when(teacherRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRegistrationService.getByTeacher(999L);
        });

        verify(classRegistrationRepository, never()).findByTeacher(any());
    }

    @Test
    void getByFitnessClass_ValidClass_ShouldReturnRegistrations() {
        List<ClassRegistration> mockRegistrations = List.of(mockRegistration);

        when(fitnessClassRepository.findById(mockFitnessClass1.getId())).thenReturn(Optional.of(mockFitnessClass1));
        when(classRegistrationRepository.findByFitnessClass(mockFitnessClass1)).thenReturn(mockRegistrations);

        List<ClassRegistration> result = classRegistrationService.getByFitnessClass(mockFitnessClass1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockRegistration);
        verify(classRegistrationRepository, times(1)).findByFitnessClass(mockFitnessClass1);
    }

    @Test
    void getByFitnessClass_ClassNotFound_ShouldThrowException() {
        when(fitnessClassRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            classRegistrationService.getByFitnessClass(999L);
        });

        verify(classRegistrationRepository, never()).findByFitnessClass(any());
    }
}
