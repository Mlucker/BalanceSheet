package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    @Query("SELECT e.account.type, SUM(e.amount) FROM JournalEntry e WHERE e.account.company.id = :companyId GROUP BY e.account.type")
    List<Object[]> sumByAccountType(Long companyId);

    @Query("SELECT e.account, SUM(e.amount) FROM JournalEntry e WHERE e.account.company.id = :companyId GROUP BY e.account")
    List<Object[]> sumByAccount(Long companyId);

    @Query("SELECT e.account.type, SUM(e.amount) FROM JournalEntry e WHERE e.account.company.id = :companyId AND YEAR(e.transaction.date) = :year GROUP BY e.account.type")
    List<Object[]> sumByAccountTypeAndYear(Long companyId, int year);

    @Query("SELECT e.account, SUM(e.amount) FROM JournalEntry e WHERE e.account.company.id = :companyId AND YEAR(e.transaction.date) = :year GROUP BY e.account")
    List<Object[]> sumByAccountAndYear(Long companyId, int year);
}
