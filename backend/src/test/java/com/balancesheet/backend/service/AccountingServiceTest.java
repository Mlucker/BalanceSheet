package com.balancesheet.backend.service;

import com.balancesheet.backend.dto.TransactionRequest;
import com.balancesheet.backend.dto.JournalEntryRequest;
import com.balancesheet.backend.model.*;
import com.balancesheet.backend.repository.AccountRepository;
import com.balancesheet.backend.repository.CompanyRepository;
import com.balancesheet.backend.repository.JournalEntryRepository;
import com.balancesheet.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private AccountingService accountingService;

    private Company testCompany;
    private Account debitAccount;
    private Account creditAccount;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Company");
        testCompany.setCurrency("USD");

        debitAccount = new Account();
        debitAccount.setId(10L);
        debitAccount.setName("Officer Salaries");
        debitAccount.setCompany(testCompany);

        creditAccount = new Account();
        creditAccount.setId(20L);
        creditAccount.setName("Checking");
        creditAccount.setCompany(testCompany);
    }

    @Test
    void recordTransaction_Successful() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setDescription("Paying Salaries");
        request.setDate(LocalDateTime.now());

        List<JournalEntryRequest> entries = new ArrayList<>();

        // Debit $1000
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountId(10L);
        debitEntry.setAmount(new BigDecimal("1000.00"));
        entries.add(debitEntry);

        // Credit -$1000
        JournalEntryRequest creditEntry = new JournalEntryRequest();
        creditEntry.setAccountId(20L);
        creditEntry.setAmount(new BigDecimal("-1000.00"));
        entries.add(creditEntry);

        request.setEntries(entries);

        // Mocks
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(debitAccount));
        when(accountRepository.findById(20L)).thenReturn(Optional.of(creditAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = accountingService.recordTransaction(request, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("Paying Salaries", result.getDescription());
        assertEquals(2, result.getEntries().size());
        assertEquals(testCompany, result.getCompany());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void recordTransaction_Unbalanced_ThrowsException() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        request.setDescription("Unbalanced Transaction");
        List<JournalEntryRequest> entries = new ArrayList<>();

        // Debit $1000
        JournalEntryRequest debitEntry = new JournalEntryRequest();
        debitEntry.setAccountId(10L);
        debitEntry.setAmount(new BigDecimal("1000.00"));
        entries.add(debitEntry);

        // No matching credit
        request.setEntries(entries);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountingService.recordTransaction(request, 1L);
        });

        assertTrue(exception.getMessage().contains("Transaction must balance"));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
