package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.PaymentScheduleElement;
import com.enfint.conveyor.dto.ScoringDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringConveyorService {

    private final CalculateFullRatingService fullRatingService;
    private final OfferCalculationService offerCalculation;


    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO) {
        log.info("************ Generating Credit ***************");
        BigDecimal rate = offerCalculation.getRate(scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());
        BigDecimal amount = scoringDataDTO.getAmount().setScale(2, RoundingMode.CEILING);
        Integer term = scoringDataDTO.getTerm();
        BigDecimal monthlyPayment = offerCalculation.getMonthlyPayment(rate, amount, term);
        BigDecimal totalAmount = offerCalculation.getFullAmount(monthlyPayment, term);
        rate = rate.add(fullRatingService.getFullRate(scoringDataDTO));
        CreditDTO creditDTO = CreditDTO
                .builder()
                .rate(rate)
                .amount(amount)
                .term(term)
                .isInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDTO.getIsSalaryClient())
                .monthlyPayment(monthlyPayment)
                .psk(totalAmount)
                .build();
        creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

        return creditDTO;
    }

    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO) {
        log.info("************ Generating Payment Schedule ***************");
        LocalDate currentDate = LocalDate.now();
        Integer term = creditDTO.getTerm();
        BigDecimal totalLoanAmount = creditDTO.getPsk();
        BigDecimal debtPayment = creditDTO.getMonthlyPayment();
        BigDecimal loanedAmount = creditDTO.getAmount();
        BigDecimal interestPaymentBase = totalLoanAmount.subtract(loanedAmount).divide(BigDecimal.valueOf(term), RoundingMode.HALF_UP);


        BigDecimal interestPayment = BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
        BigDecimal totalPayment = BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
        BigDecimal remainingDebt = totalLoanAmount;

        List<PaymentScheduleElement> paymentScheduleList = new ArrayList<>();
        paymentScheduleList.add(
                 PaymentScheduleElement.builder()
                         .number(0)
                         .date(currentDate)
                         .totalPayment(totalPayment)
                         .interestPayment(interestPayment)
                         .debtPayment(BigDecimal.ZERO.setScale(2, RoundingMode.CEILING))
                         .remainingDebt(remainingDebt)
                         .build()
        );
        currentDate = currentDate.plusMonths(1);
        for (int x = 1; x <= term; x++) {

            currentDate = currentDate.with(TemporalAdjusters.firstDayOfNextMonth());
            totalPayment = totalPayment.add(debtPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);
            interestPayment = interestPayment.add(interestPaymentBase);

            paymentScheduleList.add(
                     PaymentScheduleElement
                             .builder()
                             .number(x)
                             .date(currentDate)
                             .totalPayment(totalPayment.setScale(2, RoundingMode.CEILING))
                             .interestPayment(interestPayment.setScale(2, RoundingMode.CEILING))
                             .debtPayment(debtPayment.setScale(2, RoundingMode.CEILING))
                             .remainingDebt(remainingDebt.setScale(2, RoundingMode.CEILING))
                             .build()
            );
        }
        return paymentScheduleList;
    }
}
