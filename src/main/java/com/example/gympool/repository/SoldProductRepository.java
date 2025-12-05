package com.example.gympool.repository;

import com.example.gympool.dto.ProductStatDTO;
import com.example.gympool.entity.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoldProductRepository extends JpaRepository<SoldProduct, Long> {
    List<SoldProduct> findByBill_Id(Long billId);
    List<SoldProduct> findByProduct_Id(Long productId);

    @Query("SELECT new com.example.gympool.dto.ProductStatDTO(" +
            "p.id, p.name, p.price, COALESCE(SUM(s.quantity), 0L), " +
            "COALESCE(SUM(CASE WHEN s.soldPrice > 0 THEN s.soldPrice ELSE p.price END * s.quantity), 0.0)) " +
            "FROM SoldProduct s JOIN s.product p " +
            "GROUP BY p.id, p.name, p.price " +
            "ORDER BY COALESCE(SUM(CASE WHEN s.soldPrice > 0 THEN s.soldPrice ELSE p.price END * s.quantity), 0.0) DESC " +
            "LIMIT 5")
    List<ProductStatDTO> getProductSalesRanking();
}
