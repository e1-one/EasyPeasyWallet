package com.easypeasy.wallet.exchange.services;

import com.easypeasy.wallet.exchange.model.Currency;

import java.time.LocalDateTime;

public interface CurrencyConversionRateProvider {
    double exchangeRate(Currency from, Currency to, LocalDateTime when);
}
