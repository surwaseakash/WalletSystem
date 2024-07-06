package com.wallet.WalletSystem.service;


import com.wallet.WalletSystem.dto.WalletBalanceResponse;
import com.wallet.WalletSystem.dto.WalletResponse;
import com.wallet.WalletSystem.model.Wallet;
import com.wallet.WalletSystem.repository.TransactionRepository;
import com.wallet.WalletSystem.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class WalletServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceTest.class);

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet testWallet;

    @BeforeEach
    public void setUp() {
        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setUserId("testUser");
        testWallet.setBalance(100.0);
    }

    @Test
    public void testTopup_Success() {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        WalletResponse response = walletService.topup("testUser", 50.0);

        assertTrue(response.isStatus());
        assertEquals(150.0, response.getNewBalance());
        assertNotNull(response.getTransactionId());

        verify(walletRepository, times(1)).findByUserId("testUser");
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    public void testDeduct_Success() throws Exception {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        WalletResponse response = walletService.deduct("testUser", 50.0);

        assertTrue(response.isStatus());
        assertEquals(50.0, response.getNewBalance());
        assertNotNull(response.getTransactionId());

        verify(walletRepository, times(1)).findByUserId("testUser");
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    public void testGetBalance_Success() {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.of(testWallet));

        WalletBalanceResponse response = walletService.getBalance("testUser");

        assertEquals(100.0, response.getBalance());

        verify(walletRepository, times(1)).findByUserId("testUser");
    }

    @Test
    public void testTopup_UserNotFound() {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.topup("testUser", 50.0));

        verify(walletRepository, times(1)).findByUserId("testUser");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void testDeduct_InsufficientBalance() {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.of(testWallet));

        assertThrows(InsufficientResourcesException.class, () -> walletService.deduct("testUser", 150.0));

        verify(walletRepository, times(1)).findByUserId("testUser");
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void testConcurrentTopup() throws InterruptedException, ExecutionException {
        when(walletRepository.findByUserId("testUser")).thenReturn(Optional.of(testWallet));
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Example thread pool size
        List<Future<Void>> futures = new ArrayList<>();

        // Submitting 10 concurrent topup requests
        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(() -> {
                walletService.topup("testUser", 10.0); // Concurrent topup
                return null;
            }));
        }

        // Waiting for all threads to complete
        for (Future<Void> future : futures) {
            future.get(); // This will wait for each task to complete
        }

        // Retrieve final balance after all concurrent topups
        WalletBalanceResponse balanceResponse = walletService.getBalance("testUser");
        double finalBalance = balanceResponse.getBalance();

        // Assert the final balance (expected: initial balance + 10 * 10.0)
        assertEquals(200.0, finalBalance, "Expected final balance does not match");

        // Clean up
        executorService.shutdown();

    }
}
