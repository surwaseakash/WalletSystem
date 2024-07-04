package com.wallet.WalletSystem.service;

import com.wallet.WalletSystem.dto.WalletBalanceResponse;
import com.wallet.WalletSystem.dto.WalletResponse;
import com.wallet.WalletSystem.model.Transaction;
import com.wallet.WalletSystem.model.Wallet;
import com.wallet.WalletSystem.repository.TransactionRepository;
import com.wallet.WalletSystem.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.UUID;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public WalletResponse topup(String userId, double amount) {
        System.out.println("+++++++++++++INSIDE SERVICE++++++++++++++++" + userId + amount);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found..."));

        System.out.println("Wallet data in service +++++++++++++++++++" + wallet.getBalance());
        // Create a new Transaction entity
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("topup");

        // Save Transaction entity
        transactionRepository.save(transaction);

        // Update Wallet balance
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        // Prepare and return response
        WalletResponse response = new WalletResponse();
        response.setStatus(true);
        response.setNewBalance(wallet.getBalance());
        response.setTransactionId(transaction.getTransactionId());
        return response;
    }

    @Transactional
    public WalletResponse deduct(String userId, double amount) throws Exception {
        // Retrieve or create Wallet entity for the user
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found..."));

        // Ensure sufficient balance
        if (wallet.getBalance() < amount) {
            //throw new Exception("Insufficient balance");
            throw new InsufficientResourcesException("Insufficient balance");
        }

        // Create a new Transaction entity
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("deduct");

        // Save Transaction entity
        transactionRepository.save(transaction);

        // Update Wallet balance
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        // Prepare and return response
        WalletResponse response = new WalletResponse();
        response.setStatus(true);
        response.setNewBalance(wallet.getBalance());
        response.setTransactionId(transaction.getTransactionId());
        return response;
    }

    public WalletBalanceResponse getBalance(String userId) {
        // Retrieve Wallet entity for the user
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found..."));

        // Prepare and return balance response
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setBalance((float) wallet.getBalance());
        return response;
    }

}


