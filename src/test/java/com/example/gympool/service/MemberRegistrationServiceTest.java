package com.example.gympool.service;

import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.impl.MemberRegistrationServiceImpl;
import com.example.gympool.service.TestDataHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberRegistrationServiceTest {

    @InjectMocks
    private MemberRegistrationServiceImpl memberRegistrationService;

    @Mock private MemberRegistrationRepository memberRegistrationRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ClassScheduleRepository classScheduleRepository;

    private Member mockMember1;
    private Member mockMember2;
    private ClassSchedule mockSchedule1;
    private ClassSchedule mockSchedule2;
    private ClassSchedule mockSchedule3;
    private FitnessClass mockFitnessClass1;
    private FitnessClass mockFitnessClass2;
    private MemberRegistration mockRegistration;

    @BeforeEach
    void setUp() {
        mockMember1 = TestDataHelper.createMember("John Doe", "john@example.com", "0901234567");
        mockMember1.setId(1L);

        mockMember2 = TestDataHelper.createMember("Jane Smith", "jane@example.com", "0907654321");
        mockMember2.setId(2L);

        mockFitnessClass1 = TestDataHelper.createFitnessClass("Yoga Basics", "Beginner", "Basic yoga class");
        mockFitnessClass1.setId(1L);

        mockFitnessClass2 = TestDataHelper.createFitnessClass("Advanced Cardio", "Advanced", "High intensity cardio");
        mockFitnessClass2.setId(2L);

        mockSchedule1 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0),
                mockFitnessClass1
        );
        mockSchedule1.setId(1L);
        mockSchedule1.setCapacity(20);

        mockSchedule2 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 10, 30),
                LocalDateTime.of(2024, 1, 15, 11, 30),
                mockFitnessClass1
        );
        mockSchedule2.setId(2L);
        mockSchedule2.setCapacity(20);

        mockSchedule3 = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 16, 9, 0),
                LocalDateTime.of(2024, 1, 16, 10, 0),
                mockFitnessClass2
        );
        mockSchedule3.setId(3L);
        mockSchedule3.setCapacity(15);

        mockRegistration = TestDataHelper.createMemberRegistration(
                mockMember1,
                mockSchedule1,
                new Date()
        );
        mockRegistration.setId(100L);
    }

    // ==================== REGISTER FOR CLASS TESTS ====================

    @Test
    void registerForClass_Success_ShouldCreateRegistration() {
        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(5);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> {
            MemberRegistration reg = invocation.getArgument(0);
            reg.setId(100L);
            return reg;
        });

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getMember()).isEqualTo(mockMember1);
        assertThat(result.getClassSchedule()).isEqualTo(mockSchedule1);
        assertThat(result.getFollowDate()).isNotNull();
        verify(memberRegistrationRepository, times(1)).save(any(MemberRegistration.class));
    }

    @Test
    void registerForClass_SetFollowDate_ShouldSetCurrentDate() {
        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(5);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result.getFollowDate()).isNotNull();
        assertThat(result.getFollowDate()).isCloseTo(new Date(), 5000); // Within 5 seconds
    }

    @Test
    void registerForClass_NoConflict_ShouldRegister() {
        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void registerForClass_CapacityAvailable_ShouldRegister() {
        mockSchedule1.setCapacity(20);

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(10); // 10/20
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void registerForClass_CapacityFull_ShouldThrowException() {
        mockSchedule1.setCapacity(20);

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(20); // Full!

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());
        });

        verify(memberRegistrationRepository, never()).save(any());
    }

    @Test
    void registerForClass_NullCapacity_ShouldUseDefaultOne() {
        mockSchedule1.setCapacity(null); // Null capacity

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
        // Capacity = null → default = 1, currentRegistrations = 0 → OK
    }

    @Test
    void registerForClass_AlreadyRegistered_ShouldThrowException() {
        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.of(mockRegistration)); // Already registered!

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());
        });

        verify(memberRegistrationRepository, never()).save(any());
    }

    @Test
    void registerForClass_WithScheduleConflict_ShouldThrowException() {
        // Create overlapping schedule
        ClassSchedule overlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 30), // Overlaps with mockSchedule1 (9:00-10:00)
                LocalDateTime.of(2024, 1, 15, 10, 30),
                mockFitnessClass2
        );
        overlappingSchedule.setId(10L);

        MemberRegistration existingReg = TestDataHelper.createMemberRegistration(
                mockMember1,
                overlappingSchedule,
                new Date()
        );

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(existingReg));

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());
        });

        verify(memberRegistrationRepository, never()).save(any());
    }

    @Test
    void registerForClass_SameDateOverlappingTime_ShouldThrowException() {
        // Same date, overlapping time: 9:00-10:00 vs 9:30-10:30
        ClassSchedule overlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 9, 30),
                LocalDateTime.of(2024, 1, 15, 10, 30),
                mockFitnessClass2
        );

        MemberRegistration existingReg = TestDataHelper.createMemberRegistration(
                mockMember1,
                overlappingSchedule,
                new Date()
        );

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(existingReg));

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());
        });
    }

    @Test
    void registerForClass_SameDateNonOverlapping_ShouldRegister() {
        // Same date, but non-overlapping: 9:00-10:00 vs 10:00-11:00 (edge case)
        ClassSchedule nonOverlappingSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 15, 10, 0), // Starts exactly when mockSchedule1 ends
                LocalDateTime.of(2024, 1, 15, 11, 0),
                mockFitnessClass2
        );

        MemberRegistration existingReg = TestDataHelper.createMemberRegistration(
                mockMember1,
                nonOverlappingSchedule,
                new Date()
        );

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(existingReg));
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void registerForClass_DifferentDate_ShouldRegister() {
        // Different date - no conflict
        ClassSchedule differentDateSchedule = TestDataHelper.createClassSchedule(
                LocalDateTime.of(2024, 1, 16, 9, 0), // Different day
                LocalDateTime.of(2024, 1, 16, 10, 0),
                mockFitnessClass2
        );

        MemberRegistration existingReg = TestDataHelper.createMemberRegistration(
                mockMember1,
                differentDateSchedule,
                new Date()
        );

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(mockMember1, mockSchedule1))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(existingReg));
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberRegistration result = memberRegistrationService.registerForClass(mockMember1.getId(), mockSchedule1.getId());

        assertThat(result).isNotNull();
    }

    @Test
    void registerForClass_MemberNotFound_ShouldThrowException() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(999L, mockSchedule1.getId());
        });

        verify(memberRegistrationRepository, never()).save(any());
    }

    @Test
    void registerForClass_ScheduleNotFound_ShouldThrowException() {
        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerForClass(mockMember1.getId(), 999L);
        });

        verify(memberRegistrationRepository, never()).save(any());
    }

    // ==================== CANCEL REGISTRATION TESTS ====================

    @Test
    void cancelRegistration_Success_ShouldDelete() {
        mockRegistration.setMember(mockMember1);
        mockSchedule1.setFitnessClass(mockFitnessClass1);
        mockRegistration.setClassSchedule(mockSchedule1);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(mockRegistration));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        verify(memberRegistrationRepository, times(1)).delete(mockRegistration);
    }

    @Test
    void cancelRegistration_BulkDelete_ShouldDeleteAllSameClass() {
        // Member has 3 registrations for the same fitness class
        MemberRegistration reg1 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule1, new Date());
        reg1.setId(100L);
        
        MemberRegistration reg2 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule2, new Date());
        reg2.setId(101L);
        
        MemberRegistration reg3 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule3, new Date());
        reg3.setId(102L);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(reg1));
        when(memberRegistrationRepository.findByMember(mockMember1))
                .thenReturn(List.of(reg1, reg2, reg3));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        // Should delete reg1 and reg2 (same fitnessClass1), but not reg3 (different fitnessClass2)
        verify(memberRegistrationRepository, times(1)).delete(reg1);
        verify(memberRegistrationRepository, times(1)).delete(reg2);
        verify(memberRegistrationRepository, never()).delete(reg3);
    }

    @Test
    void cancelRegistration_NoFitnessClass_ShouldDeleteOnlyOne() {
        mockRegistration.setMember(mockMember1);
        mockSchedule1.setFitnessClass(null); // No fitness class
        mockRegistration.setClassSchedule(mockSchedule1);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        verify(memberRegistrationRepository, times(1)).delete(mockRegistration);
        verify(memberRegistrationRepository, never()).findByMember(any());
    }

    @Test
    void cancelRegistration_DifferentMember_ShouldThrowException() {
        mockRegistration.setMember(mockMember1); // Owned by member1

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));

        // Member2 tries to cancel member1's registration
        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.cancelRegistration(100L, mockMember2.getId());
        });

        verify(memberRegistrationRepository, never()).delete(any());
    }

    @Test
    void cancelRegistration_CorrectMember_ShouldDelete() {
        mockRegistration.setMember(mockMember1);
        mockSchedule1.setFitnessClass(mockFitnessClass1);
        mockRegistration.setClassSchedule(mockSchedule1);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(mockRegistration));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        verify(memberRegistrationRepository, times(1)).delete(mockRegistration);
    }

    @Test
    void cancelRegistration_MultipleSessions_ShouldDeleteAll() {
        // Member has 2 sessions of the same class
        MemberRegistration reg1 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule1, new Date());
        reg1.setId(100L);
        
        MemberRegistration reg2 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule2, new Date());
        reg2.setId(101L);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(reg1));
        when(memberRegistrationRepository.findByMember(mockMember1))
                .thenReturn(List.of(reg1, reg2));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        verify(memberRegistrationRepository, times(1)).delete(reg1);
        verify(memberRegistrationRepository, times(1)).delete(reg2);
    }

    @Test
    void cancelRegistration_OnlyOneSession_ShouldDeleteOne() {
        mockRegistration.setMember(mockMember1);
        mockSchedule1.setFitnessClass(mockFitnessClass1);
        mockRegistration.setClassSchedule(mockSchedule1);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(mockRegistration));
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(mockRegistration));

        memberRegistrationService.cancelRegistration(100L, mockMember1.getId());

        verify(memberRegistrationRepository, times(1)).delete(mockRegistration);
    }

    @Test
    void cancelRegistration_RegistrationNotFound_ShouldThrowException() {
        when(memberRegistrationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.cancelRegistration(999L, mockMember1.getId());
        });

        verify(memberRegistrationRepository, never()).delete(any());
    }

    // ==================== BULK OPERATIONS TESTS ====================

    @Test
    void registerBulk_Success_ShouldRegisterAll() {
        List<Long> scheduleIds = List.of(mockSchedule1.getId(), mockSchedule2.getId());

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(classScheduleRepository.findById(mockSchedule2.getId())).thenReturn(Optional.of(mockSchedule2));
        when(memberRegistrationRepository.findByMemberAndClassSchedule(any(), any()))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(anyLong())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<MemberRegistration> result = memberRegistrationService.registerBulk(mockMember1.getId(), scheduleIds);

        assertThat(result).hasSize(2);
        verify(memberRegistrationRepository, times(2)).save(any(MemberRegistration.class));
    }

    @Test
    void registerBulk_OneFailure_ShouldThrowException() {
        List<Long> scheduleIds = List.of(mockSchedule1.getId(), 999L); // Second one doesn't exist

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(classScheduleRepository.findById(999L)).thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMemberAndClassSchedule(any(), any()))
                .thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of());
        when(memberRegistrationRepository.countByClassScheduleId(mockSchedule1.getId())).thenReturn(0);
        when(memberRegistrationRepository.save(any(MemberRegistration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.registerBulk(mockMember1.getId(), scheduleIds);
        });
    }

    @Test
    void cancelBulkRegistration_Success_ShouldCancelAll() {
        MemberRegistration reg1 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule1, new Date());
        reg1.setId(100L);
        
        MemberRegistration reg2 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule2, new Date());
        reg2.setId(101L);

        List<Long> registrationIds = List.of(100L, 101L);

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(reg1));
        when(memberRegistrationRepository.findById(101L)).thenReturn(Optional.of(reg2));
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(reg1, reg2));

        memberRegistrationService.cancelBulkRegistration(registrationIds, mockMember1.getId());

        verify(memberRegistrationRepository, times(2)).delete(any(MemberRegistration.class));
    }

    @Test
    void cancelBulkRegistration_OneFailure_ShouldThrowException() {
        MemberRegistration reg1 = TestDataHelper.createMemberRegistration(mockMember1, mockSchedule1, new Date());
        reg1.setId(100L);

        List<Long> registrationIds = List.of(100L, 999L); // Second one doesn't exist

        when(memberRegistrationRepository.findById(100L)).thenReturn(Optional.of(reg1));
        when(memberRegistrationRepository.findById(999L)).thenReturn(Optional.empty());
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(List.of(reg1));

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.cancelBulkRegistration(registrationIds, mockMember1.getId());
        });
    }

    // ==================== QUERY METHODS TESTS ====================

    @Test
    void getByMember_ValidMember_ShouldReturnRegistrations() {
        List<MemberRegistration> mockRegistrations = List.of(mockRegistration);

        when(memberRepository.findById(mockMember1.getId())).thenReturn(Optional.of(mockMember1));
        when(memberRegistrationRepository.findByMember(mockMember1)).thenReturn(mockRegistrations);

        List<MemberRegistration> result = memberRegistrationService.getByMember(mockMember1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockRegistration);
        verify(memberRegistrationRepository, times(1)).findByMember(mockMember1);
    }

    @Test
    void getByMember_MemberNotFound_ShouldThrowException() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.getByMember(999L);
        });

        verify(memberRegistrationRepository, never()).findByMember(any());
    }

    @Test
    void getByClassSchedule_ValidSchedule_ShouldReturnRegistrations() {
        List<MemberRegistration> mockRegistrations = List.of(mockRegistration);

        when(classScheduleRepository.findById(mockSchedule1.getId())).thenReturn(Optional.of(mockSchedule1));
        when(memberRegistrationRepository.findByClassSchedule(mockSchedule1)).thenReturn(mockRegistrations);

        List<MemberRegistration> result = memberRegistrationService.getByClassSchedule(mockSchedule1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(mockRegistration);
        verify(memberRegistrationRepository, times(1)).findByClassSchedule(mockSchedule1);
    }

    @Test
    void getByClassSchedule_ScheduleNotFound_ShouldThrowException() {
        when(classScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            memberRegistrationService.getByClassSchedule(999L);
        });

        verify(memberRegistrationRepository, never()).findByClassSchedule(any());
    }
}
