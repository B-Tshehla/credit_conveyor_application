package com.enfint.conveyor.controller;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.service.OffersConveyorService;
import com.enfint.conveyor.service.ScoringConveyorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/conveyor")
@RequiredArgsConstructor
@Slf4j
public class ConveyorController {
    private final OffersConveyorService offersConveyorService;
    private final ScoringConveyorService scoringConveyorService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@RequestBody LoanApplicationRequestDTO loanApplicationRequest) {
        log.info("************ Getting Loan offers ***************");
        log.info("Getting loan offers loanApplicationRequest {}", loanApplicationRequest);

        return offersConveyorService.getLoanOfferDTOList(loanApplicationRequest);
    }

    @PostMapping("/calculation")
    public CreditDTO getCreditDTO(@RequestBody ScoringDataDTO scoringDataDTO) {
        log.info("************ Getting Credit ***************");
        log.info("Get credit scoringDataDTO {}", scoringDataDTO);
        return scoringConveyorService.getCreditDTO(scoringDataDTO);
    }
}
