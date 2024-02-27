package com.easypeasy.wallet.dao;

import com.easypeasy.wallet.model.Wallet;

import java.math.BigDecimal;

public interface WalletDao {
    Wallet getWallet(String id);

    void updateBalanceOnWallet(long id, BigDecimal newAmount);

}
