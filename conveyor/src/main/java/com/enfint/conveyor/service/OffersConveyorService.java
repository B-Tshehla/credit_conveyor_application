package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.exception.RefusalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OffersConveyorService {
    private final OfferCalculationService offerCalculationService;


    public List<LoanOfferDTO> getLoanOfferDTOList(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offersList = new ArrayList<>();
        log.info("************ Checking if Pre-scoring passed ***************");
        if (loanApplicationRequestDTO == null) {
            throw new RefusalException("Pre-scoring failed loan application is null");
        }
        offersList.add(getLoanOffer(loanApplicationRequestDTO,false, false));
        offersList.add(getLoanOffer(loanApplicationRequestDTO,false, true));
        offersList.add(getLoanOffer(loanApplicationRequestDTO, true, false));
        offersList.add(getLoanOffer(loanApplicationRequestDTO, true, true));

        log.info("Pre-scoring passed {}", offersList);

        return offersList;
    }

    private LoanOfferDTO getLoanOffer(LoanApplicationRequestDTO loanApplicationRequestDTO, Boolean isInsuranceEnable, Boolean isSalaryClient) {
        log.info("************ Generating Loan Offer ***************");
        BigDecimal requestedAmount = loanApplicationRequestDTO.getAmount();
        Integer term = loanApplicationRequestDTO.getTerm();
        BigDecimal rate = offerCalculationService.getRate(isInsuranceEnable, isSalaryClient);
        BigDecimal monthlyPayment = offerCalculationService.getMonthlyPayment(rate, requestedAmount, term);
        BigDecimal totalAmount = offerCalculationService.getFullAmount(monthlyPayment, term);

        LoanOfferDTO loanOffer = LoanOfferDTO.builder()
                .isInsuranceEnabled(isInsuranceEnable)
                .isSalaryClient(isSalaryClient)
                .requestedAmount(requestedAmount)
                .term(term)
                .totalAmount(totalAmount)
                .rate(rate)
                .monthlyPayment(monthlyPayment)
                .build();
        log.info("creating loan offer {}", loanOffer);
        return loanOffer;
    }
}