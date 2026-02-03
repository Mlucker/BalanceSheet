package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompanyIdOrderByDateDesc(Long companyId);

    @Query("SELECT t FROM Transaction t WHERE t.company.id = :companyId AND YEAR(t.date) = :year ORDER BY t.date DESC")
    List<Transaction> findByCompanyIdAndYear(@Param("companyId") Long companyId, @Param("year") int year);
}
