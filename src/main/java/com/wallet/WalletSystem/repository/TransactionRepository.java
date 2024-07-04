package com.wallet.WalletSystem.repository;

import com.wallet.WalletSystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
