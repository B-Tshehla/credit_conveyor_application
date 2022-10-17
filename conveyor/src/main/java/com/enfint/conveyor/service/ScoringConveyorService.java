package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.PaymentScheduleElement;
import com.enfint.conveyor.dto.ScoringDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringConveyorService {

    private final CalculateFullRatingService fullRatingService;
    private final CreditDTO creditDTO;
    private final ScoringDataDTO scoringDataDTO;
    private final PaymentScheduleElement paymentScheduleElement;



    public CreditDTO getCreditDTO(ScoringDataDTO scoringDataDTO){

        creditDTO.setRate(fullRatingService.getFullRate(scoringDataDTO));
        creditDTO.setAmount(scoringDataDTO.getAmount());
        creditDTO.setTerm(scoringDataDTO.getTerm());
        creditDTO.setIsInsuranceEnabled(scoringDataDTO.getIsInsuranceEnabled());
        creditDTO.setIsSalaryClient(scoringDataDTO.getIsSalaryClient());
        creditDTO.setMonthlyPayment(getMonthlyPayment());
        creditDTO.setPsk(getMonthlyPayment().multiply(BigDecimal.valueOf(scoringDataDTO.getTerm())));
        creditDTO.setPaymentSchedule(getPaymentSchedule());

        return creditDTO;
    }

    private BigDecimal getMonthlyPayment(){
        double rate = fullRatingService.getFullRate(scoringDataDTO).doubleValue();;
        double term = scoringDataDTO.getTerm();
        double amount =  scoringDataDTO.getAmount().doubleValue();;
        double numerator;
        double denominator;
        double monthlyPayment;

        rate = rate/100/12;
        numerator = amount*rate*Math.pow((1 + rate),term);
        denominator = Math.pow((1+rate),term) -1;
        monthlyPayment = numerator/denominator;

        return BigDecimal.valueOf(monthlyPayment);
    }

    private List<PaymentScheduleElement> getPaymentSchedule(){
        LocalDate currentDate =LocalDate.now();
        Integer term = creditDTO.getTerm();
        BigDecimal totalAmount = creditDTO.getPsk();
        BigDecimal monthlyPayment = creditDTO.getMonthlyPayment();
        BigDecimal loanAmount = creditDTO.getAmount();
        BigDecimal interestPayment = totalAmount.subtract(loanAmount).divide(BigDecimal.valueOf(term));
        List<PaymentScheduleElement> paymentScheduleList = new ArrayList<>();

        for(int x = 0; x < scoringDataDTO.getTerm();x++){
            currentDate = currentDate.with(TemporalAdjusters.firstDayOfNextMonth());

            paymentScheduleElement.setNumber(x);
            paymentScheduleElement.setDate(currentDate);
            paymentScheduleElement.setTotalPayment(totalAmount);
            paymentScheduleElement.setInterestPayment(interestPayment);
            paymentScheduleElement.setDebtPayment(monthlyPayment);
            paymentScheduleElement.setRemainingDebt(totalAmount.subtract(monthlyPayment));

            paymentScheduleList.add(paymentScheduleElement);
            interestPayment = interestPayment.add(interestPayment);
        }
        return paymentScheduleList;

    }


}
