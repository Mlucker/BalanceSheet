package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.Payment;
import com.balancesheet.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> recordPayment(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestBody Map<String, Object> request) {

        Long invoiceId = Long.parseLong(request.get("invoiceId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        Long cashAccountId = Long.parseLong(request.get("cashAccountId").toString());
        LocalDate paymentDate = LocalDate.parse(request.get("paymentDate").toString());
        String paymentMethod = (String) request.get("paymentMethod");
        String reference = (String) request.get("reference");

        Payment payment = paymentService.recordPayment(companyId, invoiceId, amount, cashAccountId,
                paymentDate, paymentMethod, reference);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<Map<String, Object>> getPaymentInfo(
            @PathVariable Long invoiceId) {

        BigDecimal totalPaid = paymentService.getTotalPaidForInvoice(invoiceId);
        return ResponseEntity.ok(Map.of(
                "totalPaid", totalPaid,
                "payments", paymentService.getPaymentsByInvoice(invoiceId)));
    }
}
