package com.balancesheet.backend.controller;

import com.balancesheet.backend.dto.TransactionRequest;
import com.balancesheet.backend.model.Account;
import com.balancesheet.backend.model.AccountType;
import com.balancesheet.backend.model.Transaction;
import com.balancesheet.backend.service.AccountingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class AccountingController {

    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(accountingService.createAccount(account, companyId));
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(accountingService.getAllAccounts(companyId));
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> recordTransaction(@RequestBody TransactionRequest request,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return ResponseEntity.ok(accountingService.recordTransaction(request, companyId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(accountingService.getAllTransactions(companyId, year));
    }

    @GetMapping("/financial-position")
    public ResponseEntity<Map<AccountType, BigDecimal>> getFinancialPosition(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(accountingService.getFinancialPosition(companyId, year));
    }

    @GetMapping("/financial-position/detailed")
    public ResponseEntity<Map<AccountType, List<Map<String, Object>>>> getDetailedFinancialPosition(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(accountingService.getDetailedFinancialPosition(companyId, year));
    }
}
