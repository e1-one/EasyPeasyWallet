package com.exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class GptGeneratedExchangeServiceTest {

    @Mock
    private ExchangeService.ExchangeRateProvider exchangeRateProvider;

    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exchangeService = new ExchangeService(exchangeRateProvider);
    }

    @Test
    void testExchangePositiveAmount() {
        when(exchangeRateProvider.rate(840, 978, System.currentTimeMillis()))
                .thenReturn(0.85); // Mock exchange rate

        double convertedAmount = exchangeService.exchange(100, Currency.USD, Currency.EUR);

        assertEquals(85, convertedAmount);
    }

    @Test
    void testExchangeNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            exchangeService.exchange(-100, Currency.USD, Currency.EUR);
        });
    }
}

