package org.xyonsoft.bankingapp.repository;

import org.xyonsoft.bankingapp.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUser_Username(String username);           // CHANGED — was findByUser_Username (singular)
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumberAndUser_Username(String accountNumber, String username);

    // For admin search — matches by username OR account number, case-insensitive
    List<Account> findByAccountNumberContainingIgnoreCaseOrUser_UsernameContainingIgnoreCase(
            String accountNumber, String username);
}