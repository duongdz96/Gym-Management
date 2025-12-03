package com.example.gympool.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fitness_class")
public class FitnessClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String difficultyLevel;

    private String status;

    @OneToMany(mappedBy = "fitnessClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ClassSchedule> classSchedule;
}
