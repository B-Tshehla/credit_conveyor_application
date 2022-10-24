package com.enfint.conveyor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@ControllerAdvice
public class RefusalHandler {

    @ExceptionHandler(value = {RefusalException.class})
    public ResponseEntity<Object> handleRefusalException(RefusalException e){
        Refusal refusal = new Refusal(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(refusal,HttpStatus.UNAUTHORIZED);
    }
}
