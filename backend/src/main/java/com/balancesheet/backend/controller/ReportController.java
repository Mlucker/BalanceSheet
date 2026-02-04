package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.JournalEntry;
import com.balancesheet.backend.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/pnl")
    public ResponseEntity<Map<String, Object>> getProfitAndLoss(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(reportService.getProfitAndLoss(companyId, year));
    }

    @GetMapping("/trial-balance")
    public ResponseEntity<List<Map<String, Object>>> getTrialBalance(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(reportService.getTrialBalance(companyId));
    }

    @GetMapping("/general-ledger/{accountId}")
    public ResponseEntity<List<JournalEntry>> getGeneralLedger(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "2024") int year) {
        return ResponseEntity.ok(reportService.getGeneralLedger(companyId, accountId, year));
    }
}
