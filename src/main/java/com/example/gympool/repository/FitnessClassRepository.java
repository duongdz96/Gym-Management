package com.example.gympool.repository;

import com.example.gympool.entity.FitnessClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
    Optional<FitnessClass> findByName(String name);
}
