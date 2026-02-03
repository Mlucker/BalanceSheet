package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.Invoice;
import com.balancesheet.backend.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(companyId));
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody Invoice invoice,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoice, companyId));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Invoice> approveInvoice(
            @PathVariable Long id,
            @RequestBody Map<String, Long> payload, // Expects "arAccountId"
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {

        Long arAccountId = payload.get("arAccountId");
        if (arAccountId == null) {
            throw new IllegalArgumentException("AR Account ID is required");
        }

        return ResponseEntity.ok(invoiceService.approveInvoice(id, companyId, arAccountId));
    }
}
