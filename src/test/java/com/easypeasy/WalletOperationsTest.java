package com.easypeasy;

import com.easypeasy.wallet.exchange.services.WalletOperations;
import com.easypeasy.wallet.exchange.services.WalletOperationsImpl;
import com.easypeasy.wallet.exchange.services.CurrencyConversionRateProvider;
import com.easypeasy.wallet.exchange.dao.UserDao;
import com.easypeasy.wallet.exchange.dao.WalletDao;
import com.easypeasy.wallet.exchange.model.Currency;
import com.easypeasy.wallet.exchange.model.UserProfile;
import com.easypeasy.wallet.exchange.model.Wallet;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WalletOperationsTest {

    private UserDao mockUserDao = mock(UserDao.class);
    private WalletDao mockWalletDao= mock(WalletDao.class);
    private CurrencyConversionRateProvider mockExchange = mock(CurrencyConversionRateProvider.class);
    private WalletOperations underTest = new WalletOperationsImpl(mockWalletDao, mockUserDao, mockExchange);

    @Test
    void withdraw_userCannotRequestNegativeAmount() {
        when(mockUserDao.getUserProfile(anyString()))
                .thenReturn(new UserProfile("id123", "user@gmail.com", Set.of(111111L), false));

        assertThrows(IllegalArgumentException.class, () -> underTest.withdraw("id123", -4, Currency.USD, 11111L));
    }

    @Test
    void withdraw_userCannotWithdrawFundsFromTheWalletThatDoesNotBelongToThem() {
        when(mockUserDao.getUserProfile(anyString()))
                .thenReturn(new UserProfile("id123", "user@gmail.com", Set.of(111111L), false));

        assertThrows(IllegalArgumentException.class, () -> underTest.withdraw("id123", 40, Currency.USD, 33333L));
    }

    @Test
    void withdraw_userCannotWithdrawFundsThatAreGreaterThanAvailableBalance() {
        when(mockUserDao.getUserProfile(anyString()))
                .thenReturn(new UserProfile("id123", "user@gmail.com", Set.of(111111L), false));
        when(mockWalletDao.getWallet(anyString())).thenReturn(new Wallet("111111", 100, Currency.EURO));
        assertThrows(IllegalStateException.class, () -> underTest.withdraw("id123", 1000, Currency.USD, 111111L));
    }

    @Test
    void withdraw_whenPremiumUser_onlyExchangeFeeIsTaken() {
        when(mockUserDao.getUserProfile(anyString()))
                .thenReturn(new UserProfile("id123", "user@gmail.com", Set.of(111111L), true));
        when(mockWalletDao.getWallet(anyString())).thenReturn(new Wallet("1111111", 100, Currency.EURO));
        when(mockExchange.exchangeRate(any(), any(), any())).thenReturn(1.1);

        double actualMoney = underTest.withdraw("id123", 100, Currency.USD, 111111L);
        // currency rate is applied: 100 * 1.1 = 110
        // currency exchange operational fee is 3%. 110 * 0.97 = 106.7
        assertEquals(106.7, actualMoney);
        verify(mockWalletDao).updateBalanceOnWallet(eq(111111L), any());
    }


    @Test
    void withdraw_whenPremiumUser_forRegularUser_ExchangeFeeAndServiceFeeIsTaken() {
        when(mockUserDao.getUserProfile(anyString()))
                .thenReturn(new UserProfile("id123", "user@gmail.com", Set.of(111111L), false));
        when(mockWalletDao.getWallet(anyString())).thenReturn(new Wallet("1111111", 100, Currency.EURO));
        when(mockExchange.exchangeRate(any(), any(), any())).thenReturn(1.1);

        double actualMoney = underTest.withdraw("id123", 100, Currency.USD, 111111L);
        // currency rate is applied: 100 * 1.1 = 110
        // currency exchange operational fee is 3% and service fee is 2%
        // 110 * 0.95 = 104.5
        assertEquals(104.5, actualMoney);
        verify(mockWalletDao).updateBalanceOnWallet(eq(111111L), any());
    }
}