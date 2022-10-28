package com.enfint.conveyor.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Refusal {
    private String message;
    private HttpStatus httpStatus;
    private ZonedDateTime timestamp;
}
