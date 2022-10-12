package com.enfint.conveyor.controller;

import com.enfint.conveyor.dto.LoanApplicationRequestDTO;
import com.enfint.conveyor.dto.LoanOfferDTO;
import com.enfint.conveyor.service.OffersConveyorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("conveyor")
public class ConveyorController {

    @Autowired
    RestTemplate restTemplate;
    OffersConveyorService offersConveyorService;
    LoanApplicationRequestDTO loanApplicationRequestDTO;

    @RequestMapping("/offers/{applicationId}")
    public List<LoanOfferDTO> getLoanOffers(@PathVariable("applicationId") Long applicationId){
        loanApplicationRequestDTO =restTemplate.getForObject("http://localhost:8082/deal/application"+applicationId,
                LoanApplicationRequestDTO.class);
        return offersConveyorService.getLoanOfferDTOList(loanApplicationRequestDTO,applicationId);
    }

}
