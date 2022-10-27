package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.EmploymentDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.enumModel.Gender;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.enfint.conveyor.enumModel.EmploymentStatus.EMPLOYED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ScoringConveyorServiceTest {
    private ScoringDataDTO scoringData;
    private ScoringConveyorService underTest;


    @BeforeEach
    void setUp() {
        OfferCalculationService offerCalculationService = new OfferCalculationService();
        CalculateFullRatingService calculateFullRating = new CalculateFullRatingService();
        underTest = new ScoringConveyorService(calculateFullRating, offerCalculationService);
        EmploymentDTO employment = new EmploymentDTO(
                EMPLOYED,
                "enfint",
                BigDecimal.valueOf(8_000),
                Position.WORKER,
                25,
                15
        );
        scoringData = new ScoringDataDTO(
                BigDecimal.valueOf(10000),
                10,
                "Boitumelo",
                "Tshehla",
                "Tumi",
                Gender.MALE,
                LocalDate.of(1999,1,21),
                "1234",
                "123456",
                LocalDate.of(2010,10,24),
                "Johannesburg",
                MaritalStatus.SINGLE,
                1,
                employment,
                "FNB",
                true,
                true);
    }

    @ParameterizedTest
    @ValueSource(ints = {10,24,35,48,120})
    void shouldGetPaymentScheduleBasedOnTerm(int term) {

        scoringData.setTerm(term);
        assertThat(underTest
                .getCreditDTO(scoringData)
                .getPaymentSchedule().size()).isEqualTo(term+1);
    }
}