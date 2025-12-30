package com.example.gympool.service.impl;

import com.example.gympool.entity.MembershipPlan;
import com.example.gympool.entity.Product;
import com.example.gympool.repository.MembershipPlanRepository;
import com.example.gympool.repository.ProductRepository;
import com.example.gympool.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MembershipPlanRepository membershipPlanRepository;

    @Override
    public Product addProduct(Product product, MultipartFile image) {
        product.setStatus(false);
        product.setImportDate(new Date());

        if(product.getType().equals("Membership")) throw new RuntimeException("Product cannot be membership");
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path path = Paths.get("image/products/" + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, image.getBytes());
                product.setImage("image/products/" + fileName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save image", e);
            }
        } else {
            product.setImage("image/defaults/no-image.png");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        Product existing = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(product.getName());
        existing.setType(product.getType());
        existing.setPrice(product.getPrice());
        existing.setBrand(product.getBrand());
        existing.setQuantity(product.getQuantity());
        existing.setImportDate(product.getImportDate());
        existing.setImage(product.getImage());
        existing.setUnit(product.getUnit());
        existing.setStatus(product.isStatus());

        if(existing.getType().equals("Membership")) {
            MembershipPlan plan = membershipPlanRepository.findByName(product.getName())
                    .orElseThrow(() -> new RuntimeException("Membership Plan không tồn tại với tên này!"));
            plan.setName(product.getName());
            plan.setPrice(product.getPrice());
            membershipPlanRepository.save(plan);
        }

        return productRepository.save(existing);
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        productRepository.softDeleteById(id);
    }

    @Override
    @Transactional
    public void restoreProduct(Long id) {
        productRepository.restoreById(id);
    }

    @Override
    public Product getAvailableProductById(Long id) {
        return productRepository.findByIdAndStatusFalse(id)
                .orElseThrow(() -> new RuntimeException("Product not found or is deleted"));
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAllAvailableProducts() {
        return productRepository.findAllByStatusFalse();
    }

    @Override
    public List<Product> getAllProductsForAdmin() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllAvailableProductsByType(String type) {
        return productRepository.findAllByTypeAndStatusFalse(type);
    }

    @Override
    public List<Product> getAllAvailableProductsByTypeNot(List<String> type) {
        return productRepository.findAllByTypeNotInAndStatusFalse(type);
    }

    @Override
    public List<Product> getAllAvailableProductsByTypeIn(List<String> types) {
        return productRepository.findAllByTypeInAndStatusFalse(types);
    }
}