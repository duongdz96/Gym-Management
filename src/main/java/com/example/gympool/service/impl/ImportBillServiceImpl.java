package com.example.gympool.service.impl;

import com.example.gympool.dto.ImportBillDTO;
import com.example.gympool.dto.ImportedProductDTO;
import com.example.gympool.entity.*;
import com.example.gympool.repository.*;
import com.example.gympool.service.ImportBillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Dùng spring package này tốt hơn jakarta cho rollback
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImportBillServiceImpl implements ImportBillService {

    private final ImportBillRepository importBillRepository;
    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final ManagerRepository managerRepository;

    public ImportBillServiceImpl(ImportBillRepository importBillRepository,
                                 ProductRepository productRepository,
                                 ProviderRepository providerRepository,
                                 ManagerRepository managerRepository) {
        this.importBillRepository = importBillRepository;
        this.productRepository = productRepository;
        this.providerRepository = providerRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public ImportBillDTO createImportBill(ImportBill importBill) {
        if (importBill.getProvider() == null || importBill.getProvider().getId() == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        Provider provider = providerRepository.findById(importBill.getProvider().getId())
                .orElseThrow(() -> new RuntimeException("Provider not found with id: " + importBill.getProvider().getId()));
        importBill.setProvider(provider);

        if (importBill.getManager() == null || importBill.getManager().getId() == null) {
            throw new IllegalArgumentException("Manager information is missing");
        }
        Manager manager = managerRepository.findById(importBill.getManager().getId())
                .orElseThrow(() -> new RuntimeException("Manager not found with id: " + importBill.getManager().getId()));
        importBill.setManager(manager);

        if (importBill.getDate() == null) {
            importBill.setDate(new Date());
        }

        List<Long> productIds = importBill.getImportedProducts().stream()
                .map(ip -> ip.getProduct().getId())
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        double totalBillPrice = 0.0;

        for (ImportedProduct ip : importBill.getImportedProducts()) {
            Double sellingPrice = ip.getProduct().getPrice();
            Product product = productMap.get(ip.getProduct().getId());
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + ip.getProduct().getId());
            }

            if (ip.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for product: " + product.getName());
            }
            if (ip.getImportPrice() == null || ip.getImportPrice() < 0) {
                throw new IllegalArgumentException("Import Price cannot be negative for product: " + product.getName());
            }

            ip.setProduct(product);
            ip.setImportBill(importBill);

            product.setQuantity(product.getQuantity() + ip.getQuantity());
            if (sellingPrice != null && sellingPrice > 0) {
                product.setPrice(sellingPrice);
            }

            totalBillPrice += ip.getQuantity() * ip.getImportPrice();
        }

        productRepository.saveAll(productMap.values());

        importBill.setPrice(totalBillPrice);
        ImportBill savedBill = importBillRepository.save(importBill);

        return convertToDTO(savedBill);
    }

    @Override
    public ImportBillDTO updateImportBill(Long id, ImportBill importBillDetails) {
        ImportBill bill = importBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ImportBill not found with id: " + id));

        if (importBillDetails.getProvider() != null && importBillDetails.getProvider().getId() != null) {
            Provider provider = providerRepository.findById(importBillDetails.getProvider().getId())
                    .orElseThrow(() -> new RuntimeException("Provider not found"));
            bill.setProvider(provider);
        }

        if (importBillDetails.getManager() != null && importBillDetails.getManager().getId() != null) {
            Manager manager = managerRepository.findById(importBillDetails.getManager().getId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
            bill.setManager(manager);
        }

        bill.setDate(importBillDetails.getDate());

        for (ImportedProduct oldIp : bill.getImportedProducts()) {
            Product product = oldIp.getProduct();
            int revertedQuantity = product.getQuantity() - oldIp.getQuantity();
            if (revertedQuantity < 0) revertedQuantity = 0;
            product.setQuantity(revertedQuantity);
        }
        productRepository.saveAll(bill.getImportedProducts().stream().map(ImportedProduct::getProduct).collect(Collectors.toList()));

        bill.getImportedProducts().clear();

        List<Long> productIds = importBillDetails.getImportedProducts().stream()
                .map(ip -> ip.getProduct().getId())
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        double totalBillPrice = 0.0;

        for (ImportedProduct ip : importBillDetails.getImportedProducts()) {
            Double sellingPrice = ip.getProduct().getPrice();
            Product product = productMap.get(ip.getProduct().getId());
            if (product == null) throw new RuntimeException("Product not found ID: " + ip.getProduct().getId());

            if (ip.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be > 0");
            if (ip.getImportPrice() < 0) throw new IllegalArgumentException("Import Price cannot be negative");

            ip.setProduct(product);
            ip.setImportBill(bill);
            bill.getImportedProducts().add(ip);

            product.setQuantity(product.getQuantity() + ip.getQuantity());
            if (sellingPrice != null && sellingPrice > 0) {
                product.setPrice(sellingPrice);
            }


            totalBillPrice += ip.getQuantity() * ip.getImportPrice();
        }

        productRepository.saveAll(productMap.values());
        bill.setPrice(totalBillPrice);

        ImportBill updatedBill = importBillRepository.save(bill);
        return convertToDTO(updatedBill);
    }

    @Override
    public void deleteImportBill(Long id) {
        if(!importBillRepository.existsById(id)) {
            throw new RuntimeException("ImportBill not found to delete");
        }
        importBillRepository.deleteById(id);
    }

    @Override
    public List<ImportBillDTO> getAllImportBills() {
        return importBillRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ImportBillDTO getImportBillById(Long id) {
        ImportBill bill = importBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ImportBill not found with id: " + id));
        return convertToDTO(bill);
    }

    @Override
    public void importFromCsv(MultipartFile file, Long providerId, Long managerId) {
        try (BufferedReader fileReader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Provider provider = providerRepository.findById(providerId)
                    .orElseThrow(() -> new RuntimeException("Provider not found"));

            Manager manager = managerRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"));

            ImportBill importBill = new ImportBill();
            importBill.setDate(new Date());
            importBill.setProvider(provider);
            importBill.setManager(manager);

            List<ImportedProduct> importedProducts = new ArrayList<>();
            double totalBillPrice = 0.0;

            for (CSVRecord record : csvParser) {
                Long productId = Long.parseLong(record.get("productId"));
                int quantity = Integer.parseInt(record.get("quantity"));

                double importPrice = Double.parseDouble(record.get("importPrice"));

                Double newSellingPrice = record.isMapped("price") ? Double.parseDouble(record.get("price")) : null;

                if(quantity <= 0) throw new IllegalArgumentException("Quantity in CSV must be positive");
                if(importPrice < 0) throw new IllegalArgumentException("Import Price in CSV cannot be negative");

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

                product.setQuantity(product.getQuantity() + quantity);

                if(newSellingPrice != null && newSellingPrice >= 0) {
                    product.setPrice(newSellingPrice);
                }
                productRepository.save(product);

                ImportedProduct item = new ImportedProduct();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setImportPrice(importPrice);
                item.setImportBill(importBill);

                importedProducts.add(item);

                totalBillPrice += quantity * importPrice;
            }

            importBill.setImportedProducts(importedProducts);
            importBill.setPrice(totalBillPrice);

            importBillRepository.save(importBill);

        } catch (Exception e) {
            throw new RuntimeException("Fail to parse CSV file: " + e.getMessage());
        }
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

            // Trả về giá bán hiện tại của Product để Client hiển thị
            itemDto.setPrice(item.getProduct().getPrice());
            return itemDto;
        }).collect(Collectors.toList());

        dto.setImportedProducts(itemDtos);
        return dto;
    }
}