package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.PaymentScheduleElement;
import com.enfint.conveyor.dto.ScoringDataDTO;
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
public class ScoringConveyorService {

    private final CalculateFullRatingService fullRatingService;
    private final CreditDTO creditDTO;
    Logger log = LoggerFactory.getLogger(OffersConveyorService.class);

    public ScoringConveyorService(CalculateFullRatingService fullRatingService,
                                  CreditDTO creditDTO) {
        this.fullRatingService = fullRatingService;
        this.creditDTO = creditDTO;
    }

    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO){
        log.info("************ Generating Credit ***************");
        creditDTO.setRate(fullRatingService.getFullRate(scoringDataDTO));
        creditDTO.setAmount(scoringDataDTO.getAmount().setScale(2,RoundingMode.CEILING));
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setMonthlyPayment(getMonthlyPayment(scoringDataDTO));
        creditDTO.setPsk(getMonthlyPayment(scoringDataDTO).multiply(BigDecimal.valueOf(scoringDataDTO.getTerm())));
        creditDTO.setPaymentSchedule(getPaymentSchedule());

        return creditDTO;
    }

    private BigDecimal getMonthlyPayment(ScoringDataDTO scoringDataDTO){
        log.info("************ Calculating Monthly Payment ***************");
        double rate = fullRatingService.getFullRate(scoringDataDTO).doubleValue();
        double term = scoringDataDTO.getTerm();
        double amount =  scoringDataDTO.getAmount().doubleValue();
        double numerator;
        double denominator;
        double monthlyPayment;

        rate = rate/100/12;
        numerator = amount*rate*Math.pow((1 + rate),term);
        denominator = Math.pow((1+rate),term) -1;
        monthlyPayment = numerator/denominator;
        log.info("Calculated monthly payment{}",monthlyPayment);
        return BigDecimal.valueOf(monthlyPayment).setScale(2,RoundingMode.CEILING);
    }

    private List<PaymentScheduleElement> getPaymentSchedule(){
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
