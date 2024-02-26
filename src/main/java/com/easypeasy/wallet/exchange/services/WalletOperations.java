package com.easypeasy.wallet.exchange.services;

import com.easypeasy.wallet.exchange.model.Currency;

public interface WalletOperations {
    double withdraw(String userId, double amountToWithdraw, Currency withdrawalCurrency, long walletId);
}
