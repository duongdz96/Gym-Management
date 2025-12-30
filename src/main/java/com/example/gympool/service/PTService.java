package com.example.gympool.service;

import com.example.gympool.entity.PT;

import java.util.List;

public interface PTService {
    PT create(PT pt);
    PT getById(Long id);
    List<PT> getAll();
    PT update(Long id, PT pt);
    void delete(Long id);
}
