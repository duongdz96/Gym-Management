package com.example.gympool.repository;

import com.example.gympool.dto.MonthlyRevenueDTO;
import com.example.gympool.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByReceptionist_Id(Long managerId);
    List<Bill> findByMember_id(Long memberId);
    
    List<Bill> findTop5ByOrderByDateDesc();

    @Query("SELECT SUM(b.total) FROM Bill b WHERE b.paymentStatus = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.paymentStatus = 'PENDING'")
    Long countPendingBills();

    @Query("SELECT COUNT(b) FROM Bill b WHERE b.paymentStatus = 'PAID'")
    Long countCompletedBills();

    @Query("SELECT MONTH(b.date), SUM(b.total) " +
            "FROM Bill b " +
            "WHERE YEAR(b.date) = :year AND b.paymentStatus = 'PAID' " +
            "GROUP BY MONTH(b.date) " +
            "ORDER BY MONTH(b.date)")
    List<Object[]> getMonthlyRevenue(@Param("year") int year);
    
    @Query("SELECT SUM(b.total) FROM Bill b WHERE MONTH(b.date) = :month AND YEAR(b.date) = :year AND b.paymentStatus = 'PAID'")
    Double getRevenueByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
