package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
public class OffersConveyorService {
    Logger log = LoggerFactory.getLogger(OffersConveyorService.class);

    public List<LoanOfferDTO> getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequestDTO, Long applicationId){
        List<LoanOfferDTO> offersList = new ArrayList<>();
            log.info("************ Checking if Pre-scoring passed ***************");
            if(isPreScoring(loanApplicationRequestDTO)) {

                offersList.add(getLoanOffer(loanApplicationRequestDTO,
                        new LoanOfferDTO(), false, false));
                offersList.add(getLoanOffer(loanApplicationRequestDTO,
                        new LoanOfferDTO(), true, false));
                offersList.add(getLoanOffer(loanApplicationRequestDTO,
                        new LoanOfferDTO(), false, true));
                offersList.add(getLoanOffer(loanApplicationRequestDTO,
                        new LoanOfferDTO(), true, true));
                log.info("Pre-scoring passed {}",offersList);

            } else {
                //throw new RuntimeException("PreScoring did not pass");
                log.info("Pre-scoring fail");
            }

        return offersList;
    }
    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      LoanOfferDTO loanOffer,
                                      Boolean isInsuranceEnable,
                                      Boolean isSalaryClient){
        log.info("************ Generating Loan Offer ***************");
        loanOffer.setIsInsuranceEnabled(isInsuranceEnable);
        loanOffer.setIsSalaryClient(isSalaryClient);
        loanOffer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        loanOffer.setTerm(loanApplicationRequestDTO.getTerm());
        loanOffer.setTotalAmount(BigDecimal.ZERO);
        loanOffer.setRate(BigDecimal.ZERO);
        loanOffer.setMonthlyPayment(BigDecimal.ZERO);
        log.info("creating loan offer {}",loanOffer);
        return loanOffer;
    }
      public boolean isPreScoring(LoanApplicationRequestDTO loanApplication){
          log.info("************ Validating data ***************");
        return  isAmount(loanApplication.getAmount()) &&
                isOlder(loanApplication.getBirthdate())&&
                isLetters(loanApplication.getFirstName())&&
                isLetters(loanApplication.getLastName())&&
                isLetters(loanApplication.getMiddleName())&&
                isEmail(loanApplication.getEmail())&&
                isLoanTerm(loanApplication.getTerm())&&
                isPassport(loanApplication.getPassportSeries(),loanApplication.getPassportNumber());
    }
    private boolean isLetters(String name){
        log.info("Validating name...{}",name);
        return Pattern.matches("[a-zA-Z]{2,30}", name);
    }
    private boolean isEmail(String email){
        log.info("Validating email...");
        return Pattern.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}",email);
    }
    private boolean isLoanTerm(Integer term){
        log.info("Validating term...");
        return term>6;
    }
    private boolean isPassport(String passportSeries, String passportNumber){
        log.info("Validating passport series...");
        log.info("Validating passport Number...");
        return Pattern.matches("[\\d]{4}", passportSeries) &&
                Pattern.matches("[\\d]{6}", passportNumber);
    }
    private boolean isOlder(LocalDate dob){
        log.info("Validating age...");
        return Period.between(dob,LocalDate.now()).getYears() >= 18;
    }
    private boolean isAmount(BigDecimal amount){
        log.info("Validating loan Amount...");
        return amount.compareTo(BigDecimal.valueOf(10_000)) >= 0;
    }
}
