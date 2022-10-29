package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.EmploymentDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.enumModel.EmploymentStatus;
import com.enfint.conveyor.enumModel.Gender;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import com.enfint.conveyor.exception.RefusalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.enfint.conveyor.enumModel.EmploymentStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CalculateFullRatingServiceTest {

    CalculateFullRatingService underTest;
    ScoringDataDTO scoringData;
    EmploymentDTO employment;

    @BeforeEach
    void setUp() {
        underTest = new CalculateFullRatingService();
        employment = new EmploymentDTO(
                EMPLOYED,
                "enfint",
                BigDecimal.valueOf(8_000),
                Position.WORKER,
                25,
                15
        );
        scoringData = new ScoringDataDTO(
                BigDecimal.valueOf(10_000),
                12,
                "Boitumelo",
                "Tshehla",
                "Tumi",
                Gender.MALE,
                LocalDate.of(1999, 1, 21),
                "1234",
                "123456",
                LocalDate.of(2010, 10, 24),
                "Johannesburg",
                MaritalStatus.SINGLE,
                1,
                employment,
                "FNB",
                true,
                true);
    }


    @Test
    public void shouldCalculateFullRate() {
        BigDecimal expect = BigDecimal.valueOf(3);
        assertThat(underTest.getFullRate(scoringData)).isEqualTo(expect);
    }

    @Test
    public void shouldThrowARefusalExceptionWhenUnEmployed() {
        employment.setEmploymentStatus(UNEMPLOYED);
        assertThatThrownBy(() -> {
            underTest.getFullRate(scoringData);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Refused Client is Unemployed");
    }

    @Test
    public void shouldThrowARefusalExceptionWhenAmountIs20TimesTheSalary() {
        scoringData.setAmount(BigDecimal.valueOf(100_000));
        employment.setSalary(BigDecimal.valueOf(1_000));
        assertThatThrownBy(() -> {
            underTest.getFullRate(scoringData);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Requested Amount is more than 20 times your salary, loan refused");
    }

    @Test
    public void shouldThrowARefusalExceptionWhenAgeIsOver60OrLess20() {

        assertThatThrownBy(() -> {
            scoringData.setBirthdate(LocalDate.of(1955, 1, 29));
            underTest.getFullRate(scoringData);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Refused client does not meet the age bracket");
        assertThatThrownBy(() -> {
            scoringData.setBirthdate(LocalDate.of(2004, 1, 29));
            underTest.getFullRate(scoringData);
        }).isInstanceOf(RefusalException.class)
                .hasMessageContaining("Refused client does not meet the age bracket");
    }

    @Test
    public void shouldThrowRefusalExceptionWhenExperienceIsInvalid() {
        employment.setWorkExperienceTotal(10);
        employment.setWorkExperienceCurrent(2);

        assertThatThrownBy(() -> {
            underTest.getFullRate(scoringData);
        }).isInstanceOf(RefusalException.class).hasMessageContaining("Invalid Work Experience loan refused");
    }

    @ParameterizedTest
    @EnumSource(value = EmploymentStatus.class, names = {"BUSINESS_OWNER", "SELF_EMPLOYED"})
    public void shouldCalculateRateBaseOnEmploymentStatus(EmploymentStatus status) {
        employment.setEmploymentStatus(status);
        assertThat(underTest.getFullRate(scoringData))
                .isNotNegative();
    }

    @ParameterizedTest
    @EnumSource(value = Position.class, names = {"MID_MANAGER", "TOP_MANAGER"})
    public void shouldCalculateRateBaseOnPosition(Position position) {
        employment.setPosition(position);
        assertThat(underTest.getFullRate(scoringData)).isNotZero();
    }

    @ParameterizedTest
    @EnumSource(value = MaritalStatus.class, names = {"MARRIED", "DIVORCED"})
    public void shouldCalculateRateBaseOnMaritalStatus(MaritalStatus maritalStatus) {
        scoringData.setMaritalStatus(maritalStatus);
        assertThat(underTest.getFullRate(scoringData)).isNotNegative();
    }

    @ParameterizedTest
    @EnumSource(value = Gender.class, names = {"MALE", "FEMALE"})
    public void shouldCalculateRateBaseOnGenderAndAgeBracket(Gender gender) {
        scoringData.setGender(gender);
        scoringData.setBirthdate(LocalDate.of(1982, 6, 3));
        assertThat(underTest.getFullRate(scoringData)).isNotNegative();
    }

    @Test
    public void shouldCalculateRateBaseOnNumberOfDependents() {
        scoringData.setDependentAmount(3);
        assertThat(underTest.getFullRate(scoringData)).isNotNegative();
    }
}