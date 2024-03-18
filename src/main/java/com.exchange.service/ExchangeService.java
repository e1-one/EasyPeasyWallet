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
         * @param timestamp - Unix timestamp (UTC)
         * @return conversion rate
         */
        double rate(int baseCode, int quoteCode, long timestamp);
    }
    private ExchangeRateProvider exchangeRateProvider;

    public double exchange(double amount, Currency givenCurrency, Currency toCurrency){
        // Substituted 0.0 with 1.0 → SURVIVED
        // Changed conditional boundary → SURVIVED
        if(amount < 0){
            throw new IllegalArgumentException("Money amount can't be negative value");
        }
        BigDecimal givenMoney = BigDecimal.valueOf(amount);
        // removed call to java/sql/Timestamp::getTime → SURVIVED
        long currentTime = Timestamp.valueOf(LocalDateTime.now()).getTime();
        // removed call to com/exchange/service/Currency::getCode → SURVIVED
        // removed call to com/exchange/service/Currency::getCode → SURVIVED
        double rate = exchangeRateProvider.rate(toCurrency.getCode(), givenCurrency.getCode(),
                currentTime);
        BigDecimal convertedAmount = givenMoney.multiply(BigDecimal.valueOf(rate));
        //round down to cents
        // Substituted 2 with 3 → SURVIVED
        // Removed call to java/math/BigDecimal::setScale → SURVIVED
        // replaced call to java/math/BigDecimal::setScale with receiver → SURVIVED
        convertedAmount.setScale(2, RoundingMode.DOWN);
        return convertedAmount.doubleValue();
    }

}
