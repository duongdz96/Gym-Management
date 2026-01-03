package com.example.gympool.service;

import com.example.gympool.entity.FitnessClass;

import java.util.List;
import java.util.Optional;

public interface FitnessClassService {
    List<FitnessClass> getAll();
    Optional<FitnessClass> getById(Long id);
    FitnessClass create(FitnessClass fitnessClass);
    FitnessClass update(Long id, FitnessClass fitnessClass);
    void delete(Long id);
}
