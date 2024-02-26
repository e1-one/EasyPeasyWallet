package com.easypeasy.wallet.exchange.services;

import com.easypeasy.wallet.exchange.dao.WalletDao;
import com.easypeasy.wallet.exchange.dao.UserDao;
import com.easypeasy.wallet.exchange.model.Wallet;
import com.easypeasy.wallet.exchange.model.Currency;
import com.easypeasy.wallet.exchange.model.UserProfile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class WalletOperationsImpl implements WalletOperations {
    private UserDao userDao;
    private WalletDao walletDao;
    private CurrencyConversionRateProvider currencyExchange;
    public WalletOperationsImpl(WalletDao walletDao, UserDao userDao, CurrencyConversionRateProvider currencyExchange) {
        this.walletDao = walletDao;
        this.userDao = userDao;
        this.currencyExchange = currencyExchange;
    }
    public void withdraw(String userId, double amountToWithdraw, Currency withdrawalCurrency, long walletId) {
        if (amountToWithdraw <= 0)
            throw new IllegalArgumentException("Amount to withdraw must be greater than zero");
        UserProfile userProfile = userDao.getUserProfile(userId);
        if (!userProfile.wallets().contains(walletId))
            throw new IllegalArgumentException("Wallet " + walletId + " must belong to the user " + userId);
        Wallet wallet = walletDao.getWallet(userId);
        if(wallet.balance() < amountToWithdraw)
            throw new IllegalStateException("Not enough funds on balance. " + walletId);
        BigDecimal withdrawalAmountInWalletCurrency
                = convertToCurrency(amountToWithdraw, wallet.currency(), withdrawalCurrency);
        // take our fees
        withdrawalAmountInWalletCurrency = withdrawalAmountInWalletCurrency.multiply(
                BigDecimal.valueOf(1 + calculateFees(userProfile)/100));
        walletDao.updateBalanceOnWallet(walletId,
                BigDecimal.valueOf(wallet.balance()).subtract(withdrawalAmountInWalletCurrency));
    }
    private static final double DEFAULT_TRANSACTION_FEE_IN_PERCENT = 2;
    private static final double DEFAULT_EXCHANGE_FEE_IN_PERCENT = 3;
    private double calculateFees(UserProfile userProfile){
        double transactionFee = DEFAULT_TRANSACTION_FEE_IN_PERCENT;
        if(userProfile.premiumStatus()){
            transactionFee = 0;
        }
        return transactionFee + DEFAULT_EXCHANGE_FEE_IN_PERCENT;
    }
    // Round down to the nearest cent (two decimal places)
    private static void roundToTheCent(BigDecimal value){
        value.setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal convertToCurrency(double amountToWithdraw, Currency fromCurrency, Currency toCurrency){
        BigDecimal amount = BigDecimal.valueOf(amountToWithdraw);
        if (fromCurrency != toCurrency) {
            double rate = currencyExchange.exchangeRate(toCurrency, fromCurrency, LocalDateTime.now());
            amount = amount.multiply(BigDecimal.valueOf(rate));
            roundToTheCent(amount);
        }
        return amount;
    }
}
