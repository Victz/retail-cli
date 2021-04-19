package com.example.retail.repository;

import com.example.retail.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for the Transaction entity.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
