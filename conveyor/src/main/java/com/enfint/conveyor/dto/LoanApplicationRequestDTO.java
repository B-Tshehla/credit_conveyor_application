package com.enfint.conveyor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LoanApplicationRequestDTO {
        private BigDecimal amount;
        private Integer term;
        private String firstName;
        private String lastName;
        private String middleName;
        private String email;
        private LocalDate birthdate;
        private String passportSeries;
        private String passportNumber;

        @Autowired
        public LoanApplicationRequestDTO(BigDecimal amount, Integer term, String firstName, String lastName,
                                         String middleName, String email, LocalDate birthdate, String passportSeries,
                                         String passportNumber) {
                this.amount = amount;
                this.term = term;
                this.firstName = firstName;
                this.lastName = lastName;
                this.middleName = middleName;
                this.email = email;
                this.birthdate = birthdate;
                this.passportSeries = passportSeries;
                this.passportNumber = passportNumber;
        }
}
