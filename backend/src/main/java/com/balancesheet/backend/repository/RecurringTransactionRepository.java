package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    List<RecurringTransaction> findByNextRunDateBefore(LocalDate date);

    List<RecurringTransaction> findByCompanyId(Long companyId);
}
