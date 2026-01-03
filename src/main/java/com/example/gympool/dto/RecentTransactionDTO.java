package com.example.gympool.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentTransactionDTO {
    private Long id;
    private String customerName;
    private String type;              // "Gói thành viên", "Sản phẩm", "PT Package"
    private Double amount;
    private String status;            // "PAID", "PENDING"
    private String time;              // Thời gian giao dịch (HH:mm)
}
