package com.alevya.authsber.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;


public class JwtAuthenticationException extends AuthenticationException {
    private static final Logger logger = LogManager.getLogger(JwtAuthenticationException.class);
    private HttpStatus httpStatus;
    public JwtAuthenticationException(String msg) {
        super(msg);
        logger.error("JwtAuthenticationException: " + msg);
    }
    public JwtAuthenticationException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
        logger.error("JwtAuthenticationException: " + msg);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
