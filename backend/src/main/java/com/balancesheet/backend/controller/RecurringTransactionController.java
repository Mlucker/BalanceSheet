package com.balancesheet.backend.controller;

import com.balancesheet.backend.model.Account;
import com.balancesheet.backend.model.RecurringTransaction;
import com.balancesheet.backend.repository.AccountRepository;
import com.balancesheet.backend.repository.RecurringTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recurring")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class RecurringTransactionController {

    private final RecurringTransactionRepository repository;
    private final AccountRepository accountRepository;
    private final com.balancesheet.backend.repository.CompanyRepository companyRepository;

    public RecurringTransactionController(RecurringTransactionRepository repository,
            AccountRepository accountRepository,
            com.balancesheet.backend.repository.CompanyRepository companyRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<RecurringTransaction> getAll(
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        return repository.findByCompanyId(companyId);
    }

    @PostMapping
    public ResponseEntity<RecurringTransaction> create(@RequestBody Map<String, Object> payload,
            @RequestHeader(value = "X-Company-ID", defaultValue = "1") Long companyId) {
        com.balancesheet.backend.model.Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        RecurringTransaction rt = new RecurringTransaction();
        rt.setName((String) payload.get("name"));
        rt.setDescription((String) payload.get("description"));
        rt.setAmount(new java.math.BigDecimal(payload.get("amount").toString()));
        rt.setDayOfMonth((Integer) payload.get("dayOfMonth"));
        rt.setCategory((String) payload.get("category"));
        rt.setCompany(company);

        // Initial next run date
        LocalDate nextRun = LocalDate.now()
                .withDayOfMonth(Math.min((Integer) payload.get("dayOfMonth"), LocalDate.now().lengthOfMonth()));
        if (nextRun.isBefore(LocalDate.now())) {
            nextRun = nextRun.plusMonths(1);
        }
        rt.setNextRunDate(nextRun);

        Account dr = accountRepository.findById(Long.valueOf(payload.get("debitAccountId").toString())).orElseThrow();
        Account cr = accountRepository.findById(Long.valueOf(payload.get("creditAccountId").toString())).orElseThrow();

        // Validate accounts belong to company
        if (!dr.getCompany().getId().equals(companyId) || !cr.getCompany().getId().equals(companyId)) {
            throw new IllegalArgumentException("Accounts must belong to the selected company");
        }

        rt.setDebitAccount(dr);
        rt.setCreditAccount(cr);

        return ResponseEntity.ok(repository.save(rt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
