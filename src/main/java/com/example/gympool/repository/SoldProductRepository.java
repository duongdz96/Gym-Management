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
            "p.id, p.name, p.price, SUM(s.quantity), SUM(s.soldPrice * s.quantity)) " +
            "FROM SoldProduct s JOIN s.product p " +
            "GROUP BY p.id, p.name, p.price " +
            "ORDER BY SUM(s.soldPrice * s.quantity) DESC")
    List<ProductStatDTO> getProductSalesRanking();
}
