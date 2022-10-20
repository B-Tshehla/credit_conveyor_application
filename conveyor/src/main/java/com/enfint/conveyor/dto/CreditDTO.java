package com.enfint.conveyor.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CreditDTO {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    private List<PaymentScheduleElement> paymentSchedule;

}


