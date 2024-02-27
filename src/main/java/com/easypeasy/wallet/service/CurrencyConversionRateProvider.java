package com.easypeasy.wallet.service;

import com.easypeasy.wallet.model.Currency;

import java.time.LocalDateTime;

public interface CurrencyConversionRateProvider {
    double exchangeRate(Currency from, Currency to, LocalDateTime when);
}
