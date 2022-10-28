package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.EmploymentDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.enumModel.Gender;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.enfint.conveyor.enumModel.EmploymentStatus.EMPLOYED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoringConveyorServiceTest {
    @Mock
    private OfferCalculationService offerCalculationService;
    @Mock
    private CalculateFullRatingService calculateFullRating;
    private ScoringDataDTO scoringData;
    @InjectMocks
    private ScoringConveyorService underTest;



    @BeforeEach
    void setUp() {

        underTest = new ScoringConveyorService(calculateFullRating,offerCalculationService);
        EmploymentDTO employment = new EmploymentDTO(
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

    @Test
    public void shouldGetPaymentScheduleBasedOnTerm() {
        when(offerCalculationService.getRate(true,true))
                .thenReturn(BigDecimal.valueOf(6));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6),
                BigDecimal.valueOf(10_000)
                        .setScale(2, RoundingMode.CEILING),12))
                        .thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67),12))
                .thenReturn(BigDecimal.valueOf(10_328.04));
        when(calculateFullRating.getFullRate(scoringData)).thenReturn(BigDecimal.valueOf(3));
        assertThat(underTest
                .getCreditDTO(scoringData)
                .getPaymentSchedule().size()).isEqualTo(13);
    }
}