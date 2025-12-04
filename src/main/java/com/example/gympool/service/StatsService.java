package com.example.gympool.service;

import com.example.gympool.dto.ProductStatDTO;

import java.util.List;

public interface StatsService {
    List<ProductStatDTO> getTopSellingProducts();
}
