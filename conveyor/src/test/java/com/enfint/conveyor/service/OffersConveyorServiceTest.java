package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.exception.RefusalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OffersConveyorServiceTest {
    @Mock
    private OfferCalculationService offerCalculationService;
    @InjectMocks
    private OffersConveyorService underTest;
    private LoanApplicationRequestDTO loanApplicationRequest;


    @BeforeEach
    void setUp() {

        underTest = new OffersConveyorService(offerCalculationService);
        loanApplicationRequest =
                new LoanApplicationRequestDTO(
                        BigDecimal.valueOf(10000),
                        12,
                        "Boitumelo",
                        "Tshehla",
                        "Tumi",
                        "boitumelotshehla@gmail.com",
                        LocalDate.of(1999, 1, 21),
                        "4265",
                        "698534"
                );


    }

    @Test
    public void shouldReturnListOfFourLoanOffers() {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }
    @Test
    public void shouldThrowARefusalException() {
        assertThrows(RefusalException.class,
                () ->  underTest.getLoanOfferDTOList(null));
    }

    @ParameterizedTest
    @ValueSource(doubles = {10_000.00, 50_000.56, 100_000.90, 500_873, Double.MAX_VALUE})
    public void shouldAcceptAmountGreaterThan10_000(double amount) {
        loanApplicationRequest.setAmount(BigDecimal.valueOf(amount));
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, 0.00, -10_000, 5_000, 9_999, 100})
    public void shouldThrewARefusalExceptionWhenAmountIsNullOrInvalid(double amount) {

        assertThatThrownBy(() -> {
            loanApplicationRequest.setAmount(null);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
        assertThatThrownBy(() -> {
            loanApplicationRequest.setAmount(BigDecimal.valueOf(amount));
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest()
    @ValueSource(ints = {1, 4, 10, 15, 10, 20})
    public void shouldAcceptAgeGreaterThan18(int year) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setBirthdate(LocalDate.of(2004, 10, 25));
        LocalDate actual = loanApplicationRequest.getBirthdate().minusYears(year);
        loanApplicationRequest.setBirthdate(actual);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest()
    @ValueSource(ints = {1, 4, 10, 15, 10, 20})
    public void shouldThrowRefusalExceptionWhenAgeLessThan18(int year) {
        loanApplicationRequest.setBirthdate(LocalDate.of(2004, 10, 25));
        LocalDate actual = loanApplicationRequest.getBirthdate().plusYears(year);
        loanApplicationRequest.setBirthdate(actual);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @Test
    public void shouldThrowRefusalExceptionWhenAgeIsNull() {

        loanApplicationRequest.setBirthdate(null);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenNameIsNullOrEmpty(String name) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setLastName(name);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldAcceptWhenMiddleNameIsNullOrEmpty(String name) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setMiddleName(name);
        assertThat(
                underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"562", "jam#nmc", "hye1", "官话/官話", "Guānhuà"})
    void shouldThrowRefusalExceptionWhenNameIsInvalid(String name) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setLastName(name);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"John", "uyek", "uieuwooiem"})
    public void shouldAcceptWhenNameIsValid(String name) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setLastName(name);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"#@%^%#$@#$@#.com", "@example.com", "@example.com", "あいうえお@example.com"})
    public void shouldThrowRefusalExceptionWhenEmailIsInvalid(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email@example.com", "firstname.lastname@example.com",
            "1234567890@example.com", "email@123.123.123.123"})
    public void shouldAcceptWhenEmailIsValid(String email) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setEmail(email);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenEmailIsNullOrEmpty(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, -10_000, 1, 5, 6})
    public void shouldThrewARefusalExceptionWhenTermIsNullOrInvalid(int term) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setTerm(null);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
        assertThatThrownBy(() -> {
            loanApplicationRequest.setTerm(term);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @ValueSource(ints = {7, 17, 24, 120, 240})
    public void shouldAcceptWhenTermIsMoreThanSixMonths(int term) {
        loanApplicationRequest.setTerm(term);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenPassportSeriesIsNullOrEmpty(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "123456", "tr%$#"})
    public void shouldAcceptWhenPassportSeriesIsInvalid(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "6721", "9293"})
    public void shouldAcceptWhenPassportSeriesIsValid(String passportSeries) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setPassportSeries(passportSeries);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldThrowRefusalExceptionWhenPassportNumberIsNullOrEmpty(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "1234", "tr%$#tw", "123456789"})
    public void shouldAcceptWhenPassportNumberIsInvalid(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "672135", "929733"})
    public void shouldAcceptWhenPassportNumberIsValid(String passportNumber) {
        when(offerCalculationService.getRate(anyBoolean(),anyBoolean()))
                .thenReturn(BigDecimal.valueOf(6),BigDecimal.valueOf(14),
                        BigDecimal.valueOf(16),BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000),12))
                .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        loanApplicationRequest.setPassportNumber(passportNumber);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }


}