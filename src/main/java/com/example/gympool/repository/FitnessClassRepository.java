package com.example.gympool.repository;

import com.example.gympool.entity.FitnessClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FitnessClassRepository extends JpaRepository<FitnessClass, Long> {
    Optional<FitnessClass> findByName(String name);
    Page<FitnessClass> findByStatus(String status, Pageable pageable);
    Page<FitnessClass> findAll(Pageable pageable);
}
