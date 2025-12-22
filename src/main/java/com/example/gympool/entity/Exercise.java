package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "muscle_group")
    private String muscleGroup;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; //tập như nào

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User creator;
}
