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
        //1. changed conditional boundary → KILLED
        //2. Substituted 0.0 with 1.0 → KILLED
        //4. removed conditional - replaced comparison check with false → KILLED
        // NOTE: more test cases were added.
        if (amountToWithdraw <= 0)
            throw new IllegalArgumentException("Amount to withdraw must be greater than zero");
        UserProfile userProfile = userDao.getUserProfile(userId);
        if (!userProfile.wallets().contains(walletId))
            throw new IllegalArgumentException("Wallet " + walletId + " must belong to the user " + userId);
        Wallet wallet = walletDao.getWallet(userId);
        //1. changed conditional boundary → KILLED
        // more test cases were added.
        if(wallet.balance() < amountToWithdraw)
            throw new IllegalStateException("Not enough funds on balance. " + walletId);
        //1. removed call to com/easypeasy/wallet/exchange/model/Wallet::currency → KILLED
        //NOTE: because of this mutation we had to focus on a case when fromCurrency is the same as toCurrency,
        BigDecimal withdrawalAmountInWalletCurrency = convertToCurrency(amountToWithdraw, wallet.currency(), withdrawalCurrency);
        // take our fees
        withdrawalAmountInWalletCurrency = withdrawalAmountInWalletCurrency.multiply(
                BigDecimal.valueOf(1 + calculateFees(userProfile, wallet.currency(), withdrawalCurrency)/100));
        // NOTE: an additional test has shown that we forgot to add rounding to the final multiplication
        walletDao.updateBalanceOnWallet(walletId,
                BigDecimal.valueOf(wallet.balance()).subtract(roundToTheCent(withdrawalAmountInWalletCurrency)));
    }
    private static final double DEFAULT_TRANSACTION_FEE_IN_PERCENT = 2;
    private static final double DEFAULT_EXCHANGE_FEE_IN_PERCENT = 3;
    private double calculateFees(UserProfile userProfile, Currency fromCurrency, Currency toCurrency){
        double transactionFee = DEFAULT_TRANSACTION_FEE_IN_PERCENT;
        if(userProfile.premiumStatus()){
            transactionFee = 0;
        }
        // NOTE: mutant "removed call to com/easypeasy/wallet/exchange/model/Wallet::currency → KILLED"
        // helped to discover the bug: we should take exchangeFee only when the exchange was performed.
        double exchangeFee = DEFAULT_EXCHANGE_FEE_IN_PERCENT;
        if(fromCurrency == toCurrency){
            exchangeFee = 0;
        }
        return transactionFee + exchangeFee;
    }

    // Round down to the nearest cent (two decimal places)
    private static BigDecimal roundToTheCent(BigDecimal value){
        //1. Substituted 2 with 3 → KILLED
        //2. removed call to java/math/BigDecimal::setScale → KILLED
        //3. replaced call to java/math/BigDecimal::setScale with receiver → KILLED
        //NOTE: mutants pointed out to the bug: BigDecimal is immutable, we shouldn't ignore the return
        return value.setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal convertToCurrency(double amountToWithdraw, Currency fromCurrency, Currency toCurrency){
        BigDecimal amount = BigDecimal.valueOf(amountToWithdraw);
        // replaced equality check with true → KILLED
        if (fromCurrency != toCurrency) {
            // removed call to java/time/LocalDateTime::now → KILLED
            // NOTE: we found a bug here: first and second parameters were swapped
            double rate = currencyExchange.exchangeRate(fromCurrency, toCurrency, LocalDateTime.now());
            amount = amount.multiply(BigDecimal.valueOf(rate));
            // removed call to com/easypeasy/wallet/exchange/services/WalletOperationsImpl::roundToTheCent → KILLED
            // NOTE: we found a bug here: function's return was ignored.
            return roundToTheCent(amount);
        }
        return amount;
    }
}
