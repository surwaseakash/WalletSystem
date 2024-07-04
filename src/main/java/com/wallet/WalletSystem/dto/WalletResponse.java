package com.wallet.WalletSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletResponse {

    private boolean status;
    private double newBalance;
    private String transactionId;




}

