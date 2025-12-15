package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="workout_routine")
public class WorkoutRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "muscle_group_focus")
    private String muscleGroupFocus;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "is_public")
    private boolean isPublic;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDetail> routineDetails = new ArrayList<>();
}