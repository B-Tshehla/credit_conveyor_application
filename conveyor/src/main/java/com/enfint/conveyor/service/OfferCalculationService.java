package com.enfint.conveyor.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class OfferCalculationService {
    Logger log = LoggerFactory.getLogger(OfferCalculationService.class);
    public BigDecimal getRate(boolean isInsurance, boolean isSalaryClient){
        BigDecimal rate = BigDecimal.valueOf(15);


        if(isInsurance){
            rate = rate.subtract(BigDecimal.valueOf(5));
        }
        if(isSalaryClient){
            rate = rate.subtract(BigDecimal.valueOf(4));
        }
        if (!isInsurance){
            rate = rate.add(BigDecimal.valueOf(5));
        }
        if (!isSalaryClient){
            rate = rate.add(BigDecimal.valueOf(4));
        }
        return rate;
    }
    public BigDecimal getMonthlyPayment(Double rate,Double amount,Integer term){
        log.info("************ Calculating Monthly Payment ***************");
        double numerator;
        double denominator;
        double monthlyPayment;
        rate = rate/100/12;
        numerator = amount*rate*Math.pow((1 + rate),term);
        denominator = Math.pow((1+rate),term) -1;
        monthlyPayment = numerator/denominator;
        log.info("Calculated monthly payment");
        return BigDecimal.valueOf(monthlyPayment).setScale(2, RoundingMode.CEILING);
    }

    public BigDecimal getFullAmount(BigDecimal monthlyPayment, Integer term){
        return monthlyPayment.multiply(BigDecimal.valueOf(term));
    }
}
