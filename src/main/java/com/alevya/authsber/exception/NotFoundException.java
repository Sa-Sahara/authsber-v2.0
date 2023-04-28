package com.alevya.authsber.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Requested object missing error
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

    private static final Logger logger = LogManager.getLogger(NotFoundException.class);

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
        logger.error("NotFoundException: " + message);
    }
}
