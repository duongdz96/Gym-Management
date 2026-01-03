package com.example.gympool.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class ImportBillDTO {
    private Long id;
    private Date date;
    private Double price;
    private Long providerId;
    private String providerName;
    private Long managerId; // Can be null if inferred from auth
    private List<ImportedProductDTO> importedProducts;
}
