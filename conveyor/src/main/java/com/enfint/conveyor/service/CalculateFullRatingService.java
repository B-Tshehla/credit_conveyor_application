package com.enfint.conveyor.service;

import com.enfint.conveyor.dto.EmploymentDTO;
import com.enfint.conveyor.dto.ScoringDataDTO;
import com.enfint.conveyor.enumModel.EmploymentStatus;
import com.enfint.conveyor.enumModel.MaritalStatus;
import com.enfint.conveyor.enumModel.Position;
import org.springframework.beans.factory.annotation.Autowired;

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

public class CalculateFullRatingService {
    @Autowired
    CalculateFullRatingService(){}

    public BigDecimal getFullRate(ScoringDataDTO data){

        BigDecimal fullRating = valueOf(0);
        EmploymentDTO employment = data.getEmployment();

        fullRating = fullRating.add(getMaritalStatusRating(data.getMaritalStatus()));

        if (employment.getEmploymentStatus() != UNEMPLOYED && employment.getEmploymentStatus() != EMPLOYED){
            fullRating = fullRating.add(getWorkStatusRating(employment.getEmploymentStatus()));
        }else if (employment.getEmploymentStatus() == EMPLOYED){
            fullRating = fullRating.add(getPositionRating(employment.getPosition()));
        }else if (data.getGender() == FEMALE){
            fullRating = fullRating.add(getFemaleRating(data.getBirthdate()));
        } else if (data.getGender() == MALE) {
            fullRating = fullRating.add(getMaleRating(data.getBirthdate()));
        }else if (isValidAmount(data)){
            throw new RuntimeException("Refusal");
        } else if (data.getDependentAmount() > 1) {
            fullRating = fullRating.add(BigDecimal.valueOf(1));
        } else if(employment.getWorkExperienceCurrent() < 3 && employment.getWorkExperienceTotal() < 12){
            throw new RuntimeException("Refusal");
        }else {
            throw new RuntimeException("Refusal");
        }


        return fullRating;
    }

    private BigDecimal getWorkStatusRating(EmploymentStatus employmentStatus){

        BigDecimal rate = valueOf(0);

        if (employmentStatus == SELF_EMPLOYED) {
            rate = valueOf(1);
        }
        else if (employmentStatus == BUSINESS_OWNER){
            rate = valueOf(3);
        }
        return rate;
    }

    private BigDecimal getPositionRating(Position position){
        BigDecimal rate  = valueOf(0);

        if (position == MID_MANAGER){
            rate = valueOf(-2);
        } else if (position == TOP_MANAGER) {
            rate = valueOf(-4);
        }
        return rate;
    }

    private BigDecimal getMaritalStatusRating(MaritalStatus maritalStatus){

        BigDecimal rate = valueOf(0);

        if(maritalStatus == MaritalStatus.MARRIED){
            rate = valueOf(-3);
        } else if (maritalStatus == MaritalStatus.DIVORCED) {
            rate = valueOf(1);
        }

        return rate;
    }

    private BigDecimal getFemaleRating (LocalDate birthdate){
        int years = Period.between(birthdate,LocalDate.now()).getYears();
        BigDecimal rating;
        if(years >= 35 && years <= 60);{
            rating = valueOf(3);
        }
        return rating;
    }
    private BigDecimal getMaleRating (LocalDate birthdate){
        int years = Period.between(birthdate,LocalDate.now()).getYears();
        BigDecimal rating;
        if(years >= 30 && years <= 55);{
            rating = valueOf(3);
        }
        return rating;
    }
    private boolean isValidAmount(ScoringDataDTO data){

        BigDecimal amount = data.getAmount();
        BigDecimal salary = data.getEmployment().getSalary();

        return salary.multiply(valueOf(20)).compareTo(amount) > 0;
    }
}
