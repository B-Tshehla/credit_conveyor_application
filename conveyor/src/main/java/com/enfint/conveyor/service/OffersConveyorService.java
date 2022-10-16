package com.enfint.conveyor.service;


import com.enfint.conveyor.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

@Service
public class OffersConveyorService {

    @Autowired
    private final LoanOfferDTO loanOffer;

    @Autowired
    public OffersConveyorService(LoanOfferDTO loanOffer) {
        this.loanOffer = loanOffer;
    }

    public List<LoanOfferDTO> getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequestDTO, Long applicationId){
        List<LoanOfferDTO> offersList = new ArrayList<>();

        if(isPreScoring(loanApplicationRequestDTO)) {
            loanOffer.setApplicationId(applicationId);
            offersList.add(getLoanOffer(loanApplicationRequestDTO, loanOffer, false, false));
            offersList.add(getLoanOffer(loanApplicationRequestDTO, loanOffer, true, false));
            offersList.add(getLoanOffer(loanApplicationRequestDTO, loanOffer, false, true));
            offersList.add(getLoanOffer(loanApplicationRequestDTO, loanOffer, true, true));
        }
        else {
            throw new RuntimeException("PreScoring did not pass");
        }

        return offersList;
    }
    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO,
                                      LoanOfferDTO loanOffer,
                                      Boolean isInsuranceEnable,
                                      Boolean isSalaryClient){

        loanOffer.setIsInsuranceEnabled(isInsuranceEnable);
        loanOffer.setIsSalaryClient(isSalaryClient);
        loanOffer.setRequestedAmount(loanApplicationRequestDTO.getAmount());
        loanOffer.setTerm(loanApplicationRequestDTO.getTerm());
        loanOffer.setTotalAmount(BigDecimal.valueOf(0));
        loanOffer.setRate(BigDecimal.valueOf(0));
        loanOffer.setMonthlyPayment(BigDecimal.valueOf(0));

        return loanOffer;
    }
      private boolean isPreScoring(LoanApplicationRequestDTO loanApplication){

        return isAmount(loanApplication.getAmount()) &&
                isOlder(loanApplication.getBirthdate())&&
                isLetters(loanApplication.getFirstName())&&
                isLetters(loanApplication.getLastName())&&
                isLetters(loanApplication.getMiddleName())&&
                isEmail(loanApplication.getEmail())&&
                isLoanTerm(loanApplication.getTerm())&&
                isPassport(loanApplication.getPassportSeries(),loanApplication.getPassportNumber());
    }
    private boolean isLetters(String name){

        return Pattern.matches("[a-zA-Z]{2,30}", name);
    }
    private boolean isEmail(String email){

        return Pattern.matches("[\\w\\.]{2,10}@[\\w\\.]{2,10}",email);
    }
    private boolean isLoanTerm(Integer term){
        return term>6;
    }
    private boolean isPassport(String passportSeries, String passportNumber){

        return Pattern.matches("[\\d]{4}", passportSeries) &&
                Pattern.matches("[\\d]{6}", passportNumber);
    }
    private boolean isOlder(LocalDate dob){
        return Period.between(dob,LocalDate.now()).getYears() >= 18;
    }
    private boolean isAmount(BigDecimal amount){
        return amount.compareTo(BigDecimal.valueOf(10000)) > 0;
    }
}
