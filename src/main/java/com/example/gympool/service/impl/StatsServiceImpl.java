package com.example.gympool.service.impl;

import com.example.gympool.dto.ProductStatDTO;
import com.example.gympool.repository.SoldProductRepository;
import com.example.gympool.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private SoldProductRepository soldProductRepository;

    public List<ProductStatDTO> getTopSellingProducts() {
        return soldProductRepository.getProductSalesRanking();
    }
}
