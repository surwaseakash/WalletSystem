package com.wallet.WalletSystem.service;

import com.wallet.WalletSystem.dto.WalletBalanceResponse;
import com.wallet.WalletSystem.dto.WalletResponse;
import com.wallet.WalletSystem.model.Transaction;
import com.wallet.WalletSystem.model.Wallet;
import com.wallet.WalletSystem.repository.TransactionRepository;
import com.wallet.WalletSystem.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public WalletResponse topup(String userId, double amount) {
        logger.info("Topup request received for userId: {}, amount: {}", userId, amount);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found..."));

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("topup");

        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        WalletResponse response = new WalletResponse();
        response.setStatus(true);
        response.setNewBalance(wallet.getBalance());
        response.setTransactionId(transaction.getTransactionId());
        logger.info("Topup completed successfully for userId: {}, new balance: {}", userId, wallet.getBalance());
        return response;
    }

    @Transactional
    public WalletResponse deduct(String userId, double amount) throws Exception {
        logger.info("Deduct request received for userId: {}, amount: {}", userId, amount);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found..."));

        if (wallet.getBalance() < amount) {
            throw new InsufficientResourcesException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("deduct");

        transactionRepository.save(transaction);

        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        WalletResponse response = new WalletResponse();
        response.setStatus(true);
        response.setNewBalance(wallet.getBalance());
        response.setTransactionId(transaction.getTransactionId());
        logger.info("Deduct completed successfully for userId: {}, new balance: {}", userId, wallet.getBalance());
        return response;
    }

    public WalletBalanceResponse getBalance(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found..."));

        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setBalance((float) wallet.getBalance());
        return response;
    }

}


