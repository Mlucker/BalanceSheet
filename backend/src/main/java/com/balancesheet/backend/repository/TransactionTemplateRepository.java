package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.TransactionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionTemplateRepository extends JpaRepository<TransactionTemplate, Long> {
    List<TransactionTemplate> findByCompanyId(Long companyId);
}
