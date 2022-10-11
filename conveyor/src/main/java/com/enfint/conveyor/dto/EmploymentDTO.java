package com.enfint.conveyor.dto;

import com.enfint.conveyor.enumModel.EmploymentStatus;
import com.enfint.conveyor.enumModel.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDTO {
    private Enum<EmploymentStatus> employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Enum<Position> position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
