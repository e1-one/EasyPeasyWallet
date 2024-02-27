package com.easypeasy.wallet.service;

import com.easypeasy.wallet.dao.WalletDao;
import com.easypeasy.wallet.dao.UserDao;
import com.easypeasy.wallet.model.Wallet;
import com.easypeasy.wallet.model.Currency;
import com.easypeasy.wallet.model.UserProfile;

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
        //1. changed conditional boundary → SURVIVED
        //2. Substituted 0.0 with 1.0 → SURVIVED
        //4. removed conditional - replaced comparison check with false → SURVIVED
        if (amountToWithdraw <= 0)
            throw new IllegalArgumentException("Amount to withdraw must be greater than zero");
        UserProfile userProfile = userDao.getUserProfile(userId);
        if (!userProfile.wallets().contains(walletId))
            throw new IllegalArgumentException("Wallet " + walletId + " must belong to the user " + userId);
        Wallet wallet = walletDao.getWallet(userId);
        //1. changed conditional boundary → SURVIVED
        if(wallet.balance() < amountToWithdraw)
            throw new IllegalStateException("Not enough funds on balance. " + walletId);
        BigDecimal withdrawalAmountInWalletCurrency
        //1. removed call to com/easypeasy/wallet/exchange/model/Wallet::currency → SURVIVED
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
        //1. Substituted 2 with 3 → SURVIVED
        //2. removed call to java/math/BigDecimal::setScale → SURVIVED
        //3. replaced call to java/math/BigDecimal::setScale with receiver → SURVIVED
        value.setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal convertToCurrency(double amountToWithdraw, Currency fromCurrency, Currency toCurrency){
        BigDecimal amount = BigDecimal.valueOf(amountToWithdraw);
        // replaced equality check with true → SURVIVED
        if (fromCurrency != toCurrency) {
            // removed call to java/time/LocalDateTime::now → SURVIVED
            double rate = currencyExchange.exchangeRate(toCurrency, fromCurrency, LocalDateTime.now());
            amount = amount.multiply(BigDecimal.valueOf(rate));
            // removed call to com/easypeasy/wallet/exchange/services/WalletOperationsImpl::roundToTheCent → SURVIVED
            roundToTheCent(amount);
        }
        return amount;
    }
}
