package com.example.gympool.service.impl;

import com.example.gympool.entity.PT;
import com.example.gympool.repository.PTRepository;
import com.example.gympool.service.PTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PTServiceImpl implements PTService {

    @Autowired
    private PTRepository ptRepository;

    @Override
    public PT create(PT pt) {
        return ptRepository.save(pt);
    }

    @Override
    public PT getById(Long id) {
        return ptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PT not found with id: " + id));
    }

    @Override
    public List<PT> getAll() {
        return ptRepository.findAll();
    }

    @Override
    public PT update(Long id, PT ptDetails) {
        PT pt = ptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PT not found with id: " + id));

        pt.setFullName(ptDetails.getFullName());
        pt.setEmail(ptDetails.getEmail());
        pt.setPhone(ptDetails.getPhone());
        pt.setGender(ptDetails.getGender());
        pt.setDob(ptDetails.getDob());
        // Thêm field nào trong User hay PT thì update ở đây

        return ptRepository.save(pt);
    }

    @Override
    public void delete(Long id) {
        if (!ptRepository.existsById(id)) {
            throw new RuntimeException("PT not found with id: " + id);
        }
        ptRepository.deleteById(id);
    }
}

