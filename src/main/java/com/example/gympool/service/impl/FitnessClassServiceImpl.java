package com.example.gympool.service.impl;

import com.example.gympool.entity.FitnessClass;
import com.example.gympool.repository.FitnessClassRepository;
import com.example.gympool.service.FitnessClassService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FitnessClassServiceImpl implements FitnessClassService {

    private final FitnessClassRepository fitnessClassRepository;

    public FitnessClassServiceImpl(FitnessClassRepository fitnessClassRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    public List<FitnessClass> getAll() {
        return fitnessClassRepository.findAll();
    }

    @Override
    public Optional<FitnessClass> getById(Long id) {
        return fitnessClassRepository.findById(id);
    }

    @Override
    public FitnessClass create(FitnessClass fitnessClass) {
        return fitnessClassRepository.save(fitnessClass);
    }

    @Override
    public FitnessClass update(Long id, FitnessClass fitnessClass) {
        return fitnessClassRepository.findById(id)
                .map(existing -> {
                    existing.setName(fitnessClass.getName());
                    existing.setDescription(fitnessClass.getDescription());
                    existing.setDifficultyLevel(fitnessClass.getDifficultyLevel());
                    existing.setStatus(fitnessClass.getStatus());
                    return fitnessClassRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("ClassType not found with id " + id));
    }

    @Override
    public void delete(Long id) {
        FitnessClass fitnessClass = fitnessClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FitnessClass not found with id " + id));

        fitnessClass.setStatus("INACTIVE");

        fitnessClassRepository.save(fitnessClass);
    }
}
