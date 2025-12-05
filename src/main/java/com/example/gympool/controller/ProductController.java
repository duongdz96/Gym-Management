package com.example.gympool.controller;

import com.example.gympool.entity.Product;
import com.example.gympool.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);
        Product newProduct = productService.addProduct(product, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id); // Đảm bảo update đúng id
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}") //xoa mem
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")    //lay 1 cai chua xoa mem
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getAvailableProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/name/{name}") //lay 1 cai chua xoa mem
    public ResponseEntity<Product> getProductByName(@PathVariable String name) {
        Product product = productService.getProductByName(name);
        return ResponseEntity.ok(product);
    }

    @GetMapping     //lay chua bi xoa mem
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllAvailableProducts();
        return ResponseEntity.ok(products);
    }

    // --- CÁC API MỚI CHO LOGIC XOÁ MỀM ---
    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreProduct(@PathVariable Long id) { //khoi phuc xoa mem
        productService.restoreProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/all")   //lay toan bo
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {
        List<Product> products = productService.getAllProductsForAdmin();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/type/{type}") //lay cua pt
    public ResponseEntity<List<Product>> getProductsByType(@PathVariable String type) {
        List<Product> products = productService.getAllAvailableProductsByType(type);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/exclude-types")
    public ResponseEntity<List<Product>> getProductsExcludeTypes(@RequestParam List<String> types) {

        List<Product> products = productService.getAllAvailableProductsByTypeNot(types);
        return ResponseEntity.ok(products);
    }

}