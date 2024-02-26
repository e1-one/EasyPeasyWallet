package com.easypeasy.wallet.exchange.dao;

import com.easypeasy.wallet.exchange.model.Wallet;

import java.math.BigDecimal;

public interface WalletDao {
    Wallet getWallet(String id);

    void updateBalanceOnWallet(long id, BigDecimal newAmount);

}
