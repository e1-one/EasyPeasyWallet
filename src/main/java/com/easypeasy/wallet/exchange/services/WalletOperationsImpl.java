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
    public double withdraw(String userId, double amountToWithdraw, Currency withdrawCurrency, long walletId) {
        if (amountToWithdraw <= 0)
            throw new IllegalArgumentException("Amount to withdraw must be greater than zero");
        UserProfile userProfile = userDao.getUserProfile(userId);
        if (!userProfile.wallets().contains(walletId))
            throw new IllegalArgumentException("Wallet " + walletId + " must belong to the user " + userId);
        Wallet wallet = walletDao.getWallet(userId);
        // should check after conversion
        if(wallet.balance() < amountToWithdraw){
            throw new IllegalStateException("Not enough funds on balance. " + walletId);
        }
        BigDecimal amount = convertToCurrency(amountToWithdraw, wallet.currency(), withdrawCurrency);
        // take our fees
        amount = amount.multiply(BigDecimal.valueOf(1 - calculateFees(userProfile)/100));
        walletDao.updateBalanceOnWallet(walletId, BigDecimal.valueOf(wallet.balance()).subtract(amount));
        return amount.doubleValue();
    }
    private static final double DEFAULT_TRANSACTION_FEE_IN_PERCENT = 2;
    private static final double DEFAULT_EXCHANGE_FEE_IN_PERCENT = 3;
    private double calculateFees(UserProfile userProfile){
        double transactionFee = DEFAULT_TRANSACTION_FEE_IN_PERCENT;
        if(userProfile.premiumStatus()){
            //M: negation (Non default)
            transactionFee = 0;
        }
        //M:Math
        return transactionFee + DEFAULT_EXCHANGE_FEE_IN_PERCENT;
    }
    // Round down to the nearest cent (two decimal places)
    private static void roundToTheCent(BigDecimal value){
        //M: void method calls
        value.setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal convertToCurrency(double amountToWithdraw, Currency fromCurrency, Currency toCurrency){
        BigDecimal amount = BigDecimal.valueOf(amountToWithdraw);
        //M: negate conditions
        if (fromCurrency != toCurrency) {
            //todo: will a mutation be able spot a bug here?
            double rate = currencyExchange.exchangeRate(toCurrency, fromCurrency, LocalDateTime.now());
            amount = amount.multiply(BigDecimal.valueOf(rate));
            roundToTheCent(amount);
        }
        return amount;
    }
}