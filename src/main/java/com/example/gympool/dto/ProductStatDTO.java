package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatDTO {
    private Long productId;
    private String productName;
    private Double currentPrice; // Giá hiện tại từ Product
    private Long totalQuantitySold; // Kết quả SUM(s.quantity)
    private Double totalRevenue;
}
