package com.alevya.authsber.controller;

import com.alevya.authsber.dto.UserRegistrationDto;
import com.alevya.authsber.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registration controller
 */
@Tag(name = "Registration Controller")
@RestController
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final UserService userService;
    private static final Log log = LogFactory.getLog(RegistrationController.class);

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRegistrationDto> registrationUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        log.info("registrationUser userDtoRequest: " + userRegistrationDto);
        return ResponseEntity.ok(userService.registrationUser(userRegistrationDto));
    }
}
