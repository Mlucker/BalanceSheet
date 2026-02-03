package com.balancesheet.backend.repository;

import com.balancesheet.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCompanyId(Long companyId);

    Optional<Account> findByNameAndCompanyId(String name, Long companyId);
}
