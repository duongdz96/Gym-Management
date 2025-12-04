package com.example.gympool.controller;

import com.example.gympool.dto.ProductStatDTO;
import com.example.gympool.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/products/ranking")
    public ResponseEntity<List<ProductStatDTO>> getProductRanking() {
        List<ProductStatDTO> ranking = statsService.getTopSellingProducts();
        return ResponseEntity.ok(ranking);
    }
}