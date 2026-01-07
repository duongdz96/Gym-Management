package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private LocalDateTime checkInTime;

    @Column(nullable = true)
    private LocalDateTime checkOutTime;

    // FK tới Member
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
