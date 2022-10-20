package com.enfint.conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LoanOfferDTO {
    private Long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;


}
