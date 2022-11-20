package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.exception.RefusalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        loanApplicationRequest = new LoanApplicationRequestDTO(BigDecimal.valueOf(10000), 12, "Boitumelo", "Tshehla", "Tumi", "boitumelotshehla@gmail.com", LocalDate.of(1999, 1, 21), "4265", "698534");
    }

    @Test
    public void shouldReturnListOfFourLoanOffers() {
        when(offerCalculationService.getRate(anyBoolean(), anyBoolean())).thenReturn(BigDecimal.valueOf(6), BigDecimal.valueOf(14), BigDecimal.valueOf(16), BigDecimal.valueOf(24));
        when(offerCalculationService.getMonthlyPayment(BigDecimal.valueOf(6), BigDecimal.valueOf(10_000), 12)).thenReturn(BigDecimal.valueOf(860.67));
        when(offerCalculationService.getFullAmount(BigDecimal.valueOf(860.67), 12)).thenReturn(BigDecimal.valueOf(10_328.04));
        assertThat(underTest.getLoanOfferDTOList(loanApplicationRequest).size()).isEqualTo(4);
    }

    @Test
    public void shouldThrowARefusalException() {
        assertThrows(RefusalException.class, () -> underTest.getLoanOfferDTOList(null));
    }
}