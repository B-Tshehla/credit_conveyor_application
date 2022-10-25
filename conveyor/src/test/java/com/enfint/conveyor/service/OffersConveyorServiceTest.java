package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.exception.RefusalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
class OffersConveyorServiceTest {
    OffersConveyorService underTest;
    OfferCalculationService offerCalculationService;
    LoanApplicationRequestDTO loanApplicationRequest;



    @BeforeEach
    void setUp() {
        offerCalculationService = new OfferCalculationService();
        underTest = new OffersConveyorService(offerCalculationService);
        loanApplicationRequest =
                new LoanApplicationRequestDTO(
                        BigDecimal.valueOf(10000),
                        10,
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
    void shouldReturnListOfFourLoanOffers() {
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @Test
    void shouldThrowARefusalException() {
        assertThatThrownBy(() -> {
            underTest.getLoanOfferDTOList(loanApplicationRequest = null);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @ValueSource(doubles = {10_000.00, 50_000.56, 100_000.90, 500_873, Double.MAX_VALUE})
    void shouldAcceptAmountGreaterThan10_000(double amount) {
        loanApplicationRequest.setAmount(BigDecimal.valueOf(amount));
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, 0.00, -10_000, 5_000, 9_999, 100})
    void shouldThrewARefusalExceptionWhenAmountIsNullOrInvalid(double amount) {

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
    void shouldAcceptAgeGreaterThan18(int year) {
        loanApplicationRequest.setBirthdate(LocalDate.of(2003, 10, 25));
        LocalDate actual = loanApplicationRequest.getBirthdate().minusYears(year);
        loanApplicationRequest.setBirthdate(actual);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest()
    @ValueSource(ints = {1, 4, 10, 15, 10, 20})
    void shouldThrowRefusalExceptionWhenAgeLessThan18(int year) {
        loanApplicationRequest.setBirthdate(LocalDate.of(2003, 10, 25));
        LocalDate actual = loanApplicationRequest.getBirthdate().plusYears(year);
        loanApplicationRequest.setBirthdate(actual);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @Test
    void shouldThrowRefusalExceptionWhenAgeIsNull() {

        loanApplicationRequest.setBirthdate(null);
        assertThatThrownBy(() -> underTest.getLoanOfferDTOList(loanApplicationRequest))
                .isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring failed");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowRefusalExceptionWhenNameIsNullOrEmpty(String name) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setLastName(name);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldAcceptWhenMiddleNameIsNullOrEmpty(String name) {
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
    void shouldAcceptWhenNameIsValid(String name) {
        loanApplicationRequest.setLastName(name);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"#@%^%#$@#$@#.com", "@example.com", "@example.com", "あいうえお@example.com"})
    void shouldThrowRefusalExceptionWhenEmailIsInvalid(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email@example.com", "firstname.lastname@example.com",
            "1234567890@example.com", "email@123.123.123.123"})
    void shouldAcceptWhenEmailIsValid(String email) {
        loanApplicationRequest.setEmail(email);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowRefusalExceptionWhenEmailIsNullOrEmpty(String email) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setEmail(email);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, 0, -10_000, 1, 5, 6})
    void shouldThrewARefusalExceptionWhenTermIsNullOrInvalid(int term) {
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
    void shouldAcceptWhenTermIsMoreThanSixMonths(int term) {
        loanApplicationRequest.setTerm(term);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowRefusalExceptionWhenPassportSeriesIsNullOrEmpty(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "123456", "tr%$#"})
    void shouldAcceptWhenPassportSeriesIsInvalid(String passportSeries) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportSeries(passportSeries);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234", "6721", "9293"})
    void shouldAcceptWhenPassportSeriesIsValid(String passportSeries) {
        loanApplicationRequest.setPassportSeries(passportSeries);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowRefusalExceptionWhenPassportNumberIsNullOrEmpty(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "twy32", "1234", "tr%$#tw", "123456789"})
    void shouldAcceptWhenPassportNumberIsInvalid(String passportNumber) {
        assertThatThrownBy(() -> {
            loanApplicationRequest.setPassportNumber(passportNumber);
            underTest.getLoanOfferDTOList(loanApplicationRequest);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Pre-scoring fail");
    }

    @ParameterizedTest
    @ValueSource(strings = {"123456", "672135", "929733"})
    void shouldAcceptWhenPassportNumberIsValid(String passportNumber) {
        loanApplicationRequest.setPassportNumber(passportNumber);
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }


}