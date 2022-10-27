package com.enfint.conveyor.controller;

import com.enfint.conveyor.dto.*;
import com.enfint.conveyor.enumModel.Gender;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.enfint.conveyor.enumModel.EmploymentStatus.EMPLOYED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConveyorControllerTest {

    @LocalServerPort
    private int port;
    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;
    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }
    @BeforeEach
    public void setUp(){
        baseUrl = baseUrl.concat(":").concat(port+"").concat("/conveyor");
    }

    @Test
    void shouldGetAListWithFourLoanOffers() {

        LoanApplicationRequestDTO loanApplicationRequest =
                new LoanApplicationRequestDTO(
                        BigDecimal.valueOf(10000),
                        10,
                        "Boitumelo",
                        "Tshehla",
                        "Tumi",
                        "boitumelotshehla@gmail.com",
                        LocalDate.of(1999, 1, 21),
                        "4265",
                        "698534"
                );
        List loanOfferList = restTemplate.postForObject(
                baseUrl.concat("/offers"),
                loanApplicationRequest,
                List.class
        );
        assert loanOfferList != null;
        assertThat(loanOfferList.size()).isEqualTo(4);
    }

    @Test
    void shouldGetACreditDataTransferObject() {
        EmploymentDTO employment = new EmploymentDTO(
                EMPLOYED,
                "enfint",
                BigDecimal.valueOf(8_000),
                Position.WORKER,
                25,
                15
        );
        ScoringDataDTO scoringData = new ScoringDataDTO(
                BigDecimal.valueOf(10000),
                10,
                "Boitumelo",
                "Tshehla",
                "Tumi",
                Gender.MALE,
                LocalDate.of(1999,1,21),
                "1234",
                "123456",
                LocalDate.of(2010,10,24),
                "Johannesburg",
                MaritalStatus.SINGLE,
                1,
                employment,
                "FNB",
                true,
                true);

        CreditDTO credit = restTemplate.postForObject(
                baseUrl.concat("/calculation"),
                scoringData, CreditDTO.class);

        assert credit != null;
        assertThat(credit.getPaymentSchedule().size())
                .isEqualTo(scoringData.getTerm()+1);
        assertThat(credit.getPsk()).isPositive();
    }
}