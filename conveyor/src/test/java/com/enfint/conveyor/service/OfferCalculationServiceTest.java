package com.enfint.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class OfferCalculationServiceTest {
    @InjectMocks
    OfferCalculationService underTest;

    @Test
    public void shouldCheckTheBestRate() {
        BigDecimal expected = BigDecimal.valueOf(6);
        assertThat(underTest.getRate(true, true)).isEqualTo(expected);
    }

    @Test
    public void shouldCheckTheSecondBestRate() {
        BigDecimal expected = BigDecimal.valueOf(14);
        assertThat(underTest.getRate(true, false)).isEqualTo(expected);
    }

    @Test
    public void shouldCheckTheSecondWorstRate() {
        BigDecimal expected = BigDecimal.valueOf(16);
        assertThat(underTest.getRate(false, true)).isEqualTo(expected);
    }

    @Test
    public void shouldCheckTheWorstRate() {
        BigDecimal expected = BigDecimal.valueOf(24);
        assertThat(underTest.getRate(false, false)).isEqualTo(expected);
    }

    @Test
    public void shouldCalculateMonthlyPayment() {
        BigDecimal rate = BigDecimal.valueOf(6.0);
        BigDecimal amount = BigDecimal.valueOf(10_000.00);
        Integer term = 12;
        BigDecimal expected = BigDecimal.valueOf(860.67);
        assertThat(underTest.getMonthlyPayment(rate, amount, term)).isEqualTo(expected);
    }

    @Test
    public void shouldCalculateFullAmount() {
        Integer term = 12;
        BigDecimal monthlyPayment = BigDecimal.valueOf(860.67);
        BigDecimal expected = BigDecimal.valueOf(10_328.04);

        assertThat(underTest.getFullAmount(monthlyPayment, term)).isEqualTo(expected);

    }
}