package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.PaymentScheduleElement;
import com.enfint.conveyor.dto.ScoringDataDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ScoringConveyorService {

    private final CalculateFullRatingService fullRatingService;
    private final  OfferCalculationService offerCalculation;
    Logger log = LoggerFactory.getLogger(OffersConveyorService.class);

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO){
        log.info("************ Generating Credit ***************");
        CreditDTO creditDTO = new CreditDTO();
        BigDecimal rate = offerCalculation.getRate(scoringDataDTO.getIsInsuranceEnabled(),
                scoringDataDTO.getIsSalaryClient());
        BigDecimal amount = scoringDataDTO.getAmount().setScale(2,RoundingMode.CEILING);
        Integer term = scoringDataDTO.getTerm();
        BigDecimal monthlyPayment = offerCalculation.getMonthlyPayment(rate.doubleValue(),
                amount.doubleValue(),term);
        BigDecimal totalAmount = offerCalculation.getFullAmount(monthlyPayment,term);

        rate = rate.add(fullRatingService.getFullRate(scoringDataDTO));

        creditDTO.setRate(rate);
        creditDTO.setAmount(amount);
        creditDTO.setTerm(term);
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setMonthlyPayment(monthlyPayment);
        creditDTO.setPsk(totalAmount);
        creditDTO.setPaymentSchedule(getPaymentSchedule(creditDTO));

        return creditDTO;
    }
    private List<PaymentScheduleElement> getPaymentSchedule(CreditDTO creditDTO){
        log.info("************ Generating Payment Schedule ***************");
        LocalDate currentDate =LocalDate.now();
        Integer term = creditDTO.getTerm();
        BigDecimal totalLoanAmount = creditDTO.getPsk();
        BigDecimal debtPayment = creditDTO.getMonthlyPayment();
        BigDecimal loanedAmount = creditDTO.getAmount();
        BigDecimal interestPaymentBase = totalLoanAmount.subtract(loanedAmount)
                .divide(BigDecimal.valueOf(term),RoundingMode.HALF_UP);


        BigDecimal interestPayment = BigDecimal.ZERO.setScale(2,RoundingMode.CEILING);
        BigDecimal totalPayment = BigDecimal.ZERO.setScale(2,RoundingMode.CEILING);
        BigDecimal remainingDebt = totalLoanAmount;

        List<PaymentScheduleElement> paymentScheduleList = new ArrayList<>();
        paymentScheduleList.add(new PaymentScheduleElement(
           0,
                currentDate,
                totalPayment,
                interestPayment,
                BigDecimal.ZERO.setScale(2,RoundingMode.CEILING),
                remainingDebt)
        );
        currentDate = currentDate.plusMonths(1);
        for(int x = 1; x <= term ;x++){

            currentDate = currentDate.with(TemporalAdjusters.firstDayOfNextMonth());
            totalPayment = totalPayment.add(debtPayment);
            remainingDebt = remainingDebt.subtract(debtPayment);
            interestPayment = interestPayment.add(interestPaymentBase);

            paymentScheduleList.add(new PaymentScheduleElement(x,
                    currentDate,
                    totalPayment.setScale(2, RoundingMode.CEILING),
                    interestPayment.setScale(2,RoundingMode.CEILING),
                    debtPayment.setScale(2, RoundingMode.CEILING),
                    remainingDebt.setScale(2, RoundingMode.CEILING)));
        }
        return paymentScheduleList;
    }
}
