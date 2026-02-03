package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCompanyId(Long companyId);

    List<Invoice> findByCompanyIdAndCustomerId(Long companyId, Long customerId);
}
