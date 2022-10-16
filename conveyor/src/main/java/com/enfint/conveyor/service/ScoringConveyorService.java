package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.PaymentScheduleElement;
import com.enfint.conveyor.dto.ScoringDataDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ScoringConveyorService {

    CalculateFullRatingService fullRatingService;
    CreditDTO creditDTO;
    ScoringDataDTO scoringDataDTO;
    PaymentScheduleElement paymentScheduleElement;

    @Autowired
    public ScoringConveyorService(CalculateFullRatingService fullRatingService, CreditDTO creditDTO,
                                  ScoringDataDTO scoringDataDTO, PaymentScheduleElement paymentScheduleElement) {
        this.fullRatingService = fullRatingService;
        this.creditDTO = creditDTO;
        this.scoringDataDTO = scoringDataDTO;
        this.paymentScheduleElement = paymentScheduleElement;
    }

    public CreditDTO getCreditDTO(){

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
        double rate;
        double term = scoringDataDTO.getTerm();
        double amount;
        double numerator;
        double denominator;
        double monthlyPayment;

        //convert everything to double
        rate = fullRatingService.getFullRate(scoringDataDTO).doubleValue();
        amount = scoringDataDTO.getAmount().doubleValue();

        //monthly rate
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
        }
        return paymentScheduleList;

    }


}
