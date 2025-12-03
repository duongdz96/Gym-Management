package com.example.gympool.repository;

import com.example.gympool.entity.PT;
import com.example.gympool.entity.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PTRepository extends JpaRepository<PT, Long> {
    Optional<PT> findByEmail(String email);
}
