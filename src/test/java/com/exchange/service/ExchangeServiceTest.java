package com.exchange.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExchangeServiceTest {

    private ExchangeService.ExchangeRateProvider mockExchange = mock(ExchangeService.ExchangeRateProvider.class);
    private ExchangeService underTest = new ExchangeService( mockExchange);

    @Test
    void exchange_negativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.exchange(-100, Currency.USD, Currency.EUR));
    }

    @Test
    void exchange() {
        when(mockExchange.rate(anyInt(), anyInt(), anyInt())).thenReturn(0.9);
        double actual = underTest.exchange(100, Currency.USD, Currency.EUR);
        assertEquals(90, actual);
    }
}