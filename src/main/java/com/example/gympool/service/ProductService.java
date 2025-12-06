package com.example.gympool.service;

import com.example.gympool.entity.Product;

import java.util.List;

public interface ProductService {
    Product addProduct(Product product, org.springframework.web.multipart.MultipartFile image);
    Product updateProduct(Product product);
    void softDeleteProduct(Long id);
    void restoreProduct(Long id);
    Product getAvailableProductById(Long id);
    Product getProductByName(String name);
    List<Product> getAllAvailableProducts();
    List<Product> getAllProductsForAdmin();
    List<Product> getAllAvailableProductsByType(String type);
    List<Product> getAllAvailableProductsByTypeNot(List<String> type);
    List<Product> getAllAvailableProductsByTypeIn(List<String> types);
}
