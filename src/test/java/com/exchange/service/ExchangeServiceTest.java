package com.exchange.service;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExchangeServiceTest {

    private ExchangeService.ExchangeRateProvider mockExchange = mock(ExchangeService.ExchangeRateProvider.class);
    private ExchangeService underTest = new ExchangeService( mockExchange);

    @Test
    void exchange_negativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.exchange(-100, Currency.USD, Currency.EUR));
    }

    //Mutant Killed by this test: Substituted 0.0 with 1.0
    @Test
    void exchange_amountLessThenOneCurrencyUnitIsValid() {
        when(mockExchange.rate(anyInt(), anyInt(), anyLong())).thenReturn(1.0);
        double actual = underTest.exchange(0.1, Currency.USD, Currency.EUR);
        assertEquals(0.1, actual);
    }

    //Mutant Killed by this test: Changed conditional boundary
    @Test
    void exchange_zeroAsAmountIsValid() {
        when(mockExchange.rate(anyInt(), anyInt(), anyLong())).thenReturn(1.0);
        double actual = underTest.exchange(0.0, Currency.USD, Currency.EUR);
        assertEquals(0.0, actual);
    }

    @Test
    void exchange() {
        when(mockExchange.rate(anyInt(), anyInt(), anyLong())).thenReturn(0.9);
        double actual = underTest.exchange(100, Currency.USD, Currency.EUR);
        assertEquals(90, actual);
    }

    // Mutant Killed by this test: removed call to com/exchange/service/Currency::getCode → SURVIVED
    @Test
    void exchange_rateProviderIsCalledWithCurrencyCodeValues() {
        //Note: this test helped us to identify a bug in the original version of the source code.
        when(mockExchange.rate(eq(Currency.USD.getCode()), eq(Currency.EUR.getCode()), anyLong())).thenReturn(0.9);
        double actual = underTest.exchange(100, Currency.USD, Currency.EUR);
        assertEquals(90, actual);
    }

    // Mutant Killed by this test: removed call to java/sql/Timestamp::getTime → SURVIVED
    @Test
    void exchange_verifyThatCurrencyRateIsCalledWithACurrentTimestamp() {
        // NOTE: another approach is to use PowerMock to mock static methods
        underTest.exchange(100, Currency.USD, Currency.EUR);
        verify(mockExchange).rate(anyInt(), anyInt(), longThat(actualTimeStampLong -> {
            //check that diff between actual invocation and actual time is less than 1 second
            return Timestamp.valueOf(LocalDateTime.now()).getTime() - actualTimeStampLong < 1000;
        }));
    }

    // Mutants Killed by this test:
    // Substituted 2 with 3 → SURVIVED
    // Removed call to java/math/BigDecimal::setScale → SURVIVED
    // replaced call to java/math/BigDecimal::setScale with receiver → SURVIVED
    @Test
    void exchange_verifyThatCurrencyRate() {
        when(mockExchange.rate(anyInt(), anyInt(), anyLong())).thenReturn(0.999999);
        // NOTE: another approach is to use PowerMock to mock static methods
        double actual = underTest.exchange(100, Currency.USD, Currency.EUR);
        assertEquals(99.99, actual);
    }
}