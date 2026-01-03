package com.example.gympool.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String type;
    private Double price;
    private String brand;
    private Integer quantity;
    private Date importDate;
    private String image;
    private String unit;
    private boolean status;
}
