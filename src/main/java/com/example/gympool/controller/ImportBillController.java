package com.example.gympool.controller;

import com.example.gympool.dto.ImportBillDTO;
import com.example.gympool.dto.ImportedProductDTO;
import com.example.gympool.entity.*;
import com.example.gympool.service.ImportBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/import-bills")
@CrossOrigin(origins = "*")
public class ImportBillController {

    @Autowired
    private ImportBillService importBillService;

    @GetMapping
    public ResponseEntity<List<ImportBillDTO>> getAllImportBills() {
        return ResponseEntity.ok(importBillService.getAllImportBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportBillDTO> getImportBillById(@PathVariable Long id) {
        return ResponseEntity.ok(importBillService.getImportBillById(id));
    }

    @PostMapping
    public ResponseEntity<ImportBillDTO> createImportBill(@RequestBody ImportBillDTO dto) {
        ImportBill billEntity = new ImportBill();
        if (dto.getProviderId() != null) {
            Provider provider = new Provider();
            provider.setId(dto.getProviderId());
            billEntity.setProvider(provider);
        }
        if (dto.getManagerId() != null) {
            Manager manager = new Manager();
            manager.setId(dto.getManagerId());
            billEntity.setManager(manager);
        }

        if (dto.getImportedProducts() != null) {
            List<ImportedProduct> products = dto.getImportedProducts().stream().map(itemDto -> {
                ImportedProduct item = new ImportedProduct();
                Product p = new Product();
                p.setId(itemDto.getProductId());

                p.setPrice(itemDto.getPrice());

                item.setProduct(p);
                item.setQuantity(itemDto.getQuantity());

                item.setImportPrice(itemDto.getImportPrice());

                return item;
            }).collect(Collectors.toList());

            billEntity.setImportedProducts(products);
        }

        return ResponseEntity.ok(importBillService.createImportBill(billEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImportBillDTO> updateImportBill(
            @PathVariable Long id,
            @RequestBody ImportBill importBill
    ) {
        return ResponseEntity.ok(importBillService.updateImportBill(id, importBill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImportBill(@PathVariable Long id) {
        importBillService.deleteImportBill(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("providerId") Long providerId,
            @RequestParam("managerId") Long managerId) {
        importBillService.importFromCsv(file, providerId, managerId);
        return ResponseEntity.ok("Imported successfully from CSV");
    }

    public ImportBillDTO convertToDTO(ImportBill bill) {
        ImportBillDTO dto = new ImportBillDTO();
        dto.setId(bill.getId());
        dto.setDate(bill.getDate());
        dto.setPrice(bill.getPrice());

        if (bill.getProvider() != null) {
            dto.setProviderId(bill.getProvider().getId());
            dto.setProviderName(bill.getProvider().getName());
        }

        if (bill.getManager() != null) {
            dto.setManagerId(bill.getManager().getId());
        }

        List<ImportedProductDTO> itemDtos = bill.getImportedProducts().stream().map(item -> {
            ImportedProductDTO itemDto = new ImportedProductDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setProductBrand(item.getProduct().getBrand());
            itemDto.setProductUnit(item.getProduct().getUnit());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setImportPrice(item.getImportPrice());

            itemDto.setPrice(item.getProduct().getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setImportedProducts(itemDtos);
        return dto;
    }
}