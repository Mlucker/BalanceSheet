package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByCompanyId(Long companyId);
}
