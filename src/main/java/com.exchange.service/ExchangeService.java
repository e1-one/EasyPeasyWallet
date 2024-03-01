package com.exchange.service;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
public class ExchangeService {

    public interface ExchangeRateProvider {
        /**
         * accepts two Currency codes (ISO 4217)
         * @param baseCode code of base currency
         * @param quoteCode code of quote currency
         * @param timestamp - unixTimestamp 32 bit value
         * @return conversion rate
         */
        double rate(int baseCode, int quoteCode, int timestamp);
    }

    private ExchangeRateProvider exchangeRateProvider;

    public double exchange(double amount, Currency givenCurrency, Currency toCurrency){
        if(amount < 0){
            throw new IllegalArgumentException("Money amount can't be negative value");
        }
        BigDecimal givenMoney = BigDecimal.valueOf(amount);
        int currentTime = (int) Timestamp.valueOf(LocalDateTime.now()).getTime();
        double rate = exchangeRateProvider.rate(toCurrency.getCode(), givenCurrency.getCode(), currentTime);
        BigDecimal convertedAmount = givenMoney.multiply(BigDecimal.valueOf(rate));
        //round down to cents
        convertedAmount.setScale(2, RoundingMode.DOWN);
        return convertedAmount.doubleValue();
    }

    public int sum(int a, int b){
        if(a > b){
            return -5;
        }
        return a + b;
    }

}
