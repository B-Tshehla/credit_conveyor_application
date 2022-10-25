package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.exception.RefusalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j

public class OffersConveyorService {
    OfferCalculationService offerCalculationService;

    OffersConveyorService(OfferCalculationService offersConveyorService){
        this.offerCalculationService = offersConveyorService;
    }

    public List<LoanOfferDTO> getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offersList = new ArrayList<>();
        log.info("************ Checking if Pre-scoring passed ***************");
        if (loanApplicationRequestDTO == null) {
            throw new RefusalException("Pre-scoring failed loan application is null");
        }
        isPreScoring(loanApplicationRequestDTO);
        offersList.add(getLoanOffer(loanApplicationRequestDTO, new LoanOfferDTO(), false, false));
        offersList.add(getLoanOffer(loanApplicationRequestDTO, new LoanOfferDTO(), false, true));
        offersList.add(getLoanOffer(loanApplicationRequestDTO, new LoanOfferDTO(), true, false));
        offersList.add(getLoanOffer(loanApplicationRequestDTO, new LoanOfferDTO(), true, true));

        log.info("Pre-scoring passed {}", offersList);

        return offersList;
    }

    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, LoanOfferDTO loanOffer, Boolean isInsuranceEnable, Boolean isSalaryClient) {
        log.info("************ Generating Loan Offer ***************");
        BigDecimal requestedAmount = loanApplicationRequestDTO.getAmount();
        Integer term = loanApplicationRequestDTO.getTerm();
        BigDecimal rate = offerCalculationService.getRate(isInsuranceEnable, isSalaryClient);
        BigDecimal monthlyPayment = offerCalculationService.getMonthlyPayment(rate.doubleValue(), requestedAmount.doubleValue(), term);
        BigDecimal totalAmount = offerCalculationService.getFullAmount(monthlyPayment, term);

        loanOffer.setIsInsuranceEnabled(isInsuranceEnable);
        loanOffer.setIsSalaryClient(isSalaryClient);
        loanOffer.setRequestedAmount(requestedAmount);
        loanOffer.setTerm(term);
        loanOffer.setTotalAmount(totalAmount);
        loanOffer.setRate(rate);
        loanOffer.setMonthlyPayment(monthlyPayment);
        log.info("creating loan offer {}", loanOffer);
        return loanOffer;
    }

    private void isPreScoring(LoanApplicationRequestDTO loanApplication) {
        log.info("************ Validating data ***************");
        validateAmount(loanApplication.getAmount());
        validateAge(loanApplication.getBirthdate());
        validateName(loanApplication.getFirstName());
        validateName(loanApplication.getLastName());
        blankMiddleNameCheck(loanApplication.getMiddleName());
        validateEmail(loanApplication.getEmail());
        validateLoanTerm(loanApplication.getTerm());
        passportNumberValidation(loanApplication.getPassportNumber());
        validatePassportSeries(loanApplication.getPassportSeries());
    }

    private void validateName(String name) {
        log.info("Validating name...{}", name);
        if (name == null || name.isEmpty()) {
            throw new RefusalException("Pre-scoring failed name is null");
        } else if (Pattern.matches("[a-zA-Z]{2,30}", name)) {
            log.info("Valid name passed!");
        } else {
            throw new RefusalException("Pre-scoring failed name is not valid");
        }
    }

    private void blankMiddleNameCheck(String name) {
        if (name == null || name.isEmpty()) {
            log.info("MiddleName is Empty...");
        } else {
            validateName(name);
        }
    }

    private void validateEmail(String email) {
        log.info("Validating email...");
        if (email == null || email.isEmpty()) {
            throw new RefusalException("Pre-scoring failed email is empty");
        } else if (Pattern.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}", email)) {
            log.info("Valid email passed!");
        } else {
            throw new RefusalException("Pre-scoring failed email is invalid");
        }
    }

    private void validateLoanTerm(Integer term) {
        log.info("Validating term...");
        if (term == null) {
            throw new RefusalException("Pre-scoring failed term is null");
        } else if (term > 6) {
            log.info("Term is more than 6 months passed!");
        } else {
            throw new RefusalException("Pre-scoring failed term is less than 6 months");
        }
    }

    private void validatePassportSeries(String passportSeries) {
        log.info("Validating passport series...");
        if (passportSeries == null || passportSeries.isEmpty()) {
            throw new RefusalException("Pre-scoring failed passport series is null");
        } else if (Pattern.matches("[\\d]{4}", passportSeries)) {
            log.info("Valid passport series passed!");
        } else {
            throw new RefusalException("Pre-scoring failed invalid passport series");
        }
    }

    private void passportNumberValidation(String passportNumber) {
        log.info("Validating passport Number...");
        if (passportNumber == null || passportNumber.isEmpty()) {
            throw new RefusalException("Pre-scoring failed passport number is null");
        } else if (Pattern.matches("[\\d]{6}", passportNumber)) {
            log.info("Valid passport number passed!");
        } else {
            throw new RefusalException("Pre-scoring failed passport number is invalid");
        }
    }

    private void validateAge(LocalDate dob) {
        log.info("Validating age...");
        if (dob == null) {
            throw new RefusalException("Pre-scoring failed age is null");
        } else if (Period.between(dob, LocalDate.now()).getYears() >= 18) {
            log.info("Valid age passed!");
        } else {
            throw new RefusalException("Pre-scoring failed client younger than 18");
        }
    }

    private void validateAmount(BigDecimal amount) {
        log.info("Validating loan Amount...");
        if (amount == null) {
            throw new RefusalException("Pre-scoring failed amount is null");
        } else if (amount.compareTo(BigDecimal.valueOf(10_000)) >= 0) {
            log.info("Valid amount passed!");
        } else {
            throw new RefusalException("Pre-scoring failed amount is less than 10,000.00");
        }
    }
}
