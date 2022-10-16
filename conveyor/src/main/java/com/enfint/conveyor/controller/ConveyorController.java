package com.enfint.conveyor.controller;

import com.enfint.conveyor.dto.CreditDTO;
import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.service.OffersConveyorService;
import com.enfint.conveyor.service.ScoringConveyorService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("conveyor")
public class ConveyorController {
    RestTemplate restTemplate;

    OffersConveyorService offersConveyorService;
    LoanApplicationRequestDTO loanApplicationRequestDTO;
    ScoringDataDTO scoringDataDTO;
    ScoringConveyorService scoringConveyorService;

    @RequestMapping("/offers/{applicationId}")
    public List<LoanOfferDTO> getLoanOffers(@PathVariable("applicationId") Long applicationId){
        loanApplicationRequestDTO =restTemplate.getForObject("http://localhost:8082/deal/application"+applicationId,
                LoanApplicationRequestDTO.class);
        return offersConveyorService.getLoanOfferDTOList(loanApplicationRequestDTO,applicationId);
    }

    @RequestMapping("/calculation")
    public CreditDTO getCreditDTO(){
        scoringDataDTO = restTemplate.getForObject("http://localhost:8082/deal/calculation",ScoringDataDTO.class);

        return scoringConveyorService.getCreditDTO();
    }


}
