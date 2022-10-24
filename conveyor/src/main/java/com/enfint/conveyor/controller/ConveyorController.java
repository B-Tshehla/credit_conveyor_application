package com.enfint.conveyor.controller;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.service.OffersConveyorService;
import com.enfint.conveyor.service.ScoringConveyorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
public class ConveyorController {
    private final OffersConveyorService offersConveyorService;
    private final ScoringConveyorService scoringConveyorService;

    private final Logger log = LoggerFactory.getLogger(ConveyorController.class);

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest){
        log.info("************ Getting Loan offers ***************");
        log.info("Get loan offers with application request {}", loanApplicationRequest);

        return offersConveyorService.getLoanOfferDTOList(loanApplicationRequest);
    }

    @PostMapping("/calculation")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO){
        log.info("************ Getting Credit ***************");
        log.info("Validate date and calculate credit {}", scoringDataDTO);
        return scoringConveyorService.getCreditDTO(scoringDataDTO);
    }


}
