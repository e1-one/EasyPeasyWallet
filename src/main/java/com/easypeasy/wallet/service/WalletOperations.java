package com.easypeasy.wallet.service;

import com.easypeasy.wallet.model.Currency;

public interface WalletOperations {
    void withdraw(String userId, double amountToWithdraw, Currency withdrawalCurrency, long walletId);
}
