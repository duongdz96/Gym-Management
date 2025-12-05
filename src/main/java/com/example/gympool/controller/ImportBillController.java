package com.example.gympool.controller;

import com.example.gympool.dto.ImportBillDTO;
import com.example.gympool.dto.ImportedProductDTO;
import com.example.gympool.entity.*;
import com.example.gympool.service.ImportBillService;
import com.example.gympool.repository.ProviderRepository;
import com.example.gympool.repository.ProductRepository;
import com.example.gympool.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/import-bills")
@CrossOrigin(origins = "*")
public class ImportBillController {

    @Autowired
    private ImportBillService importBillService;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @GetMapping
    public List<ImportBillDTO> getAllImportBills() {
        List<ImportBill> bills = importBillService.getAllImportBills();
        return bills.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ImportBillDTO> createImportBill(@RequestBody ImportBillDTO dto) {
        ImportBill bill = new ImportBill();
        bill.setDate(new Date()); // Use server time
        
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        bill.setProvider(provider);

        // Get current authenticated user
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
        if (authentication == null || !authentication.isAuthenticated()) {
             throw new RuntimeException("User not authenticated");
        }
        
        String email = authentication.getName(); // Should be email from CustomUserDetails
        
        Manager manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user is not a Manager or not found. Email: " + email));
                
        bill.setManager(manager);

        List<ImportedProduct> items = new ArrayList<>();
        if (dto.getImportedProducts() != null) {
            for (ImportedProductDTO itemDto : dto.getImportedProducts()) {
                ImportedProduct item = new ImportedProduct();
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setImportPrice(itemDto.getImportPrice());
                if (itemDto.getPrice() != null) {
                    item.setPrice(itemDto.getPrice());
                }
                // The service handles calculating total and updating stock
                items.add(item);
            }
        }
        bill.setImportedProducts(items);

        ImportBill savedBill = importBillService.createImportBill(bill);
        return ResponseEntity.ok(convertToDTO(savedBill));
    }

    private ImportBillDTO convertToDTO(ImportBill bill) {
        ImportBillDTO dto = new ImportBillDTO();
        dto.setId(bill.getId());
        dto.setDate(bill.getDate());
        dto.setPrice(bill.getPrice());
        dto.setProviderId(bill.getProvider().getId());
        dto.setProviderName(bill.getProvider().getName());
        dto.setManagerId(bill.getManager().getId());
        
        List<ImportedProductDTO> itemDtos = bill.getImportedProducts().stream().map(item -> {
            ImportedProductDTO itemDto = new ImportedProductDTO();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setProductBrand(item.getProduct().getBrand());
            itemDto.setProductUnit(item.getProduct().getUnit());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setImportPrice(item.getImportPrice());
            return itemDto;
        }).collect(Collectors.toList());
        
        dto.setImportedProducts(itemDtos);
        return dto;
    }
}
