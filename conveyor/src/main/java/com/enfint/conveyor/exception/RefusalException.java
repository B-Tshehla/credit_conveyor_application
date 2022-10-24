package com.enfint.conveyor.exception;

public class RefusalException extends RuntimeException {
    public RefusalException(String message) {
        super(message);
    }

    public RefusalException(String message, Throwable cause) {
        super(message, cause);
    }
}
