package com.example.gympool.service;

import com.example.gympool.dto.ImportBillDTO;
import com.example.gympool.entity.ImportBill;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportBillService {
    List<ImportBillDTO> getAllImportBills();

    ImportBillDTO getImportBillById(Long id);

    ImportBillDTO createImportBill(ImportBill importBill);

    ImportBillDTO updateImportBill(Long id, ImportBill importBill);

    void deleteImportBill(Long id);

    void importFromCsv(MultipartFile file, Long providerId, Long managerId);

    ImportBillDTO convertToDTO(ImportBill bill);
}
