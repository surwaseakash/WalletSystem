package com.wallet.WalletSystem.controller;

import com.wallet.WalletSystem.dto.WalletBalanceResponse;
import com.wallet.WalletSystem.dto.WalletResponse;
import com.wallet.WalletSystem.dto.WalletTransactionRequest;
import com.wallet.WalletSystem.model.Wallet;
import com.wallet.WalletSystem.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/topup")
    public ResponseEntity<WalletResponse> topupWallet(@Valid @RequestBody WalletTransactionRequest request) {
        System.out.println("++++++++++++++++++++Insde COntroller++++++++++++" + request.getUserId() + request.getAmount());
        WalletResponse response = walletService.topup(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deduct")
    public ResponseEntity<WalletResponse> deductWallet(@Valid @RequestBody WalletTransactionRequest request) throws Exception {
        WalletResponse response = walletService.deduct(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

   @GetMapping("/balance")
    public ResponseEntity<WalletBalanceResponse> getBalance(@Valid @RequestParam String userId) {
        WalletBalanceResponse response = walletService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

}
