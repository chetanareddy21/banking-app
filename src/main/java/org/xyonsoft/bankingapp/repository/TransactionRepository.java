package org.xyonsoft.bankingapp.repository;

import org.xyonsoft.bankingapp.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_IdOrderByTimestampDesc(Long accountId);
}
