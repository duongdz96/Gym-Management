package com.example.gympool.controller;

import com.example.gympool.dto.ProductDTO;
import com.example.gympool.entity.Product;
import com.example.gympool.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public List<Product> getAllProducts() {
        // Return all products (including active/inactive ones for admin management)
        return productService.getAllProductsForAdmin();
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestParam("product") String productJson,
                                                 @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product createdProduct = productService.addProduct(product, image);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            throw new RuntimeException("Error creating product", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // We use soft delete logic here
        productService.softDeleteProduct(id);
        return ResponseEntity.ok().build();
    }
}