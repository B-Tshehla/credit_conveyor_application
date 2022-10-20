package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.EmploymentDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.enumModel.EmploymentStatus;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static com.enfint.conveyor.enumModel.EmploymentStatus.*;
import static com.enfint.conveyor.enumModel.EmploymentStatus.BUSINESS_OWNER;
import static com.enfint.conveyor.enumModel.Gender.FEMALE;
import static com.enfint.conveyor.enumModel.Gender.MALE;
import static com.enfint.conveyor.enumModel.Position.MID_MANAGER;
import static com.enfint.conveyor.enumModel.Position.TOP_MANAGER;
import static java.math.BigDecimal.valueOf;

@Service
@NoArgsConstructor
public class CalculateFullRatingService {
    Logger log = LoggerFactory.getLogger(CalculateFullRatingService.class);
    public BigDecimal getFullRate(ScoringDataDTO data){
        log.info("************ Calculating full rating ***************");
        BigDecimal fullRating = BigDecimal.ZERO;
        EmploymentDTO employment = data.getEmployment();

        fullRating = fullRating.add(getMaritalStatusRating(data.getMaritalStatus()));

        if (employment.getEmploymentStatus() != UNEMPLOYED && employment.getEmploymentStatus() != EMPLOYED){
            fullRating = fullRating.add(getWorkStatusRating(employment.getEmploymentStatus()));
            log.info("Employment rating {}",fullRating);
        }
        if (employment.getEmploymentStatus() == EMPLOYED){
            fullRating = fullRating.add(getPositionRating(employment.getPosition()));
            log.info("Positional rating {}",fullRating);
        }
        if (data.getGender() == FEMALE){
            fullRating = fullRating.add(getFemaleRating(data.getBirthdate()));
            log.info("Female rating {}",fullRating);
        }
        if (data.getGender() == MALE) {
            fullRating = fullRating.add(getMaleRating(data.getBirthdate()));
            log.info("Male rating {}",fullRating);
        }
        if (isValidAmount(data)){
            log.info("Invalid Amount loan refused");
            //throw new RuntimeException("Refusal");
        }
        if (data.getDependentAmount() > 1) {
            fullRating = fullRating.add(BigDecimal.valueOf(1));
            log.info("Male rating {}",fullRating);
        }
        if(employment.getWorkExperienceCurrent() < 3 && employment.getWorkExperienceTotal() < 12){
            log.info("Invalid Work Experience loan refused");
            //throw new RuntimeException("Refusal");
        }
        if(isNotEmployed(data.getEmployment())){
            log.info("Refused Client is Unemployed");
            //throw new RuntimeException("Refusal");
        }
        if(isNotValidAge(data.getBirthdate())){
            log.info("Refused client does not meet the age bracket");
            //throw new RuntimeException("Refusal");
        }

        log.info("Full Rating {} ", fullRating);
        return fullRating;
    }

    private BigDecimal getWorkStatusRating(EmploymentStatus employmentStatus){
        log.info("************ Employment Status Rating ***************");
        BigDecimal rate = BigDecimal.ZERO;

        if (employmentStatus == SELF_EMPLOYED) {
            rate = valueOf(1);
            log.info("Self employed rate {}",rate);
        }
        else if (employmentStatus == BUSINESS_OWNER){
            rate = valueOf(3);
            log.info("Business owner rate {}",rate);
        }
        return rate;
    }

    private BigDecimal getPositionRating(Position position){
        BigDecimal rate = BigDecimal.ZERO;
        log.info("************ Position Rating ***************");
        if (position == MID_MANAGER){
            rate = valueOf(-2);
            log.info("Middle Manager rate {}",rate);
        } else if (position == TOP_MANAGER) {
            rate = valueOf(-4);
            log.info("Top Manager rate {}",rate);
        }
        return rate;
    }

    private BigDecimal getMaritalStatusRating(MaritalStatus maritalStatus){
        log.info("************ Marital Status Rate ***************");
        BigDecimal rate = BigDecimal.ZERO;

        if(maritalStatus == MaritalStatus.MARRIED){
            rate = valueOf(-3);
            log.info("Married rate {}",rate);
        } else if (maritalStatus == MaritalStatus.DIVORCED) {
            rate = valueOf(1);
            log.info("Divorced rate {}",rate);
        }

        return rate;
    }

    private BigDecimal getFemaleRating (LocalDate birthdate){
        log.info("************ Female Rate ***************");
        int years = Period.between(birthdate,LocalDate.now()).getYears();
        BigDecimal rate = BigDecimal.ZERO;

        if(years >= 35 && years <= 60);{
            rate = valueOf(3);
            log.info("Female aged {} rate{}",years,rate);
        }
        return rate;
    }
    private BigDecimal getMaleRating (LocalDate birthdate){
        log.info("************ Male Rate ***************");
        int years = Period.between(birthdate,LocalDate.now()).getYears();
        BigDecimal rate = BigDecimal.ZERO;
        if(years >= 30 && years <= 55);{
            rate = valueOf(3);
            log.info("Male aged {} rate {}",years,rate);
        }
        return rate;
    }
    private boolean isValidAmount(ScoringDataDTO data){
        log.info("************ Validate Amount ***************");
        BigDecimal amount = data.getAmount();
        BigDecimal salary = data.getEmployment().getSalary();

        return salary.multiply(valueOf(20)).compareTo(amount) < 0;
    }
    private boolean isNotValidAge(LocalDate birthdate){
        log.info("************ Validate Age ***************");
        return Period.between(birthdate, LocalDate.now()).getYears() < 20 ||
                Period.between(birthdate, LocalDate.now()).getYears() > 60;
    }

    private boolean isNotEmployed(EmploymentDTO employmentDTO){
        return employmentDTO.getEmploymentStatus() == UNEMPLOYED;
    }
}
