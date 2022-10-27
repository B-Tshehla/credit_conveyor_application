package com.enfint.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OfferCalculationServiceTest {
    OfferCalculationService underTest;

    @BeforeEach
    void setUp() {
        underTest = new OfferCalculationService();
    }

    @Test
    void shouldCheckTheBestRate() {
        BigDecimal expected = BigDecimal.valueOf(6);
        assertThat(underTest.getRate(true, true)).isEqualTo(expected);
    }

    @Test
    void shouldCheckTheSecondBestRate() {
        BigDecimal expected = BigDecimal.valueOf(14);
        assertThat(underTest.getRate(true, false)).isEqualTo(expected);
    }

    @Test
    void shouldCheckTheSecondWorstRate() {
        BigDecimal expected = BigDecimal.valueOf(16);
        assertThat(underTest.getRate(false, true)).isEqualTo(expected);
    }

    @Test
    void shouldCheckTheWorstRate() {
        BigDecimal expected = BigDecimal.valueOf(24);
        assertThat(underTest.getRate(false, false)).isEqualTo(expected);
    }

    @Test
    void shouldCalculateMonthlyPayment() {
        Double rate = 6.0;
        Double amount = 10_000.00;
        Integer term = 12;
        BigDecimal expected = BigDecimal.valueOf(860.67);
        assertThat(underTest.getMonthlyPayment(rate, amount, term)).isEqualTo(expected);
    }

    @Test
    void shouldCalculateFullAmount() {
        Integer term = 12;
        BigDecimal monthlyPayment = BigDecimal.valueOf(860.67);
        BigDecimal expected = BigDecimal.valueOf(10_328.04);

        assertThat(underTest.getFullAmount(monthlyPayment, term)).isEqualTo(expected);

    }
}