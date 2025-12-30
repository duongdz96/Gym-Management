package com.example.gympool.dto;

import lombok.Data;

@Data
public class ImportedProductDTO {
    private Long productId;
    private String productName;
    private String productBrand;
    private String productUnit;
    private int quantity;
    private Double importPrice;
    private Double price;
}
