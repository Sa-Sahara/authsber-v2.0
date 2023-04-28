package com.alevya.authsber.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Incoming request validation error
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final Logger logger = LogManager.getLogger(BadRequestException.class);

    public BadRequestException() {
    }
    public BadRequestException(String message) {
        super(message);
        logger.error("BadRequestException: " + message);
    }
}
