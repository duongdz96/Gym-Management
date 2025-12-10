package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="student_class_attendance")
public class StudentClassAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20)
    // PRESENT, ABSENT
    private String status;

    private LocalDateTime checkedInAt;

    @ManyToOne
    @JoinColumn(name = "checked_in_by_id", nullable = true)
    private Teacher checkedInBy;

    private String notes;

}