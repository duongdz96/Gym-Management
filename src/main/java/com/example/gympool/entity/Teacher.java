package com.example.gympool.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Table(name="teacher")
public class Teacher extends User {
    private String position;
    private String specialize;
}