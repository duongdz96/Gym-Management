package com.example.gympool.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Double price;

    @Column(length = 50)
    private String brand;

    private Integer quantity;

    @Column(name = "import_date")
    private Date importDate;

    @Column(length = 255)
    private String image;

    @Column(length = 50)
    private String unit;

    @Column(name = "status", nullable = false, columnDefinition = "boolean default false")
    private boolean status = false;     //isDeleted
}