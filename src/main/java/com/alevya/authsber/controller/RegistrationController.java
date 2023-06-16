package com.alevya.authsber.controller;

import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.security.UserDetailsServiceImpl;
import com.alevya.authsber.security.UserPrincipal;
import com.alevya.authsber.service.RegistrationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for receiving main data
 */
@CrossOrigin(value = "http://localhost:3000")
@Hidden
@RestController
//@SecurityRequirement(name = "JWT Authentication")
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;


    public RegistrationController(UserDetailsServiceImpl userDetailsService,
                                  RegistrationService registrationService,
                                  JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.registrationService = registrationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Request for generate security code and send user by sms")
@Secured("ROLE_NOVERY")
    @GetMapping(value = "/phone/send")
    public ResponseEntity<String> sendPhoneCode(@RequestHeader("Authorization") String jwtToken) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService
                .loadUserByUsername(jwtTokenProvider.getPhoneEmail(jwtToken));
        return ResponseEntity.ok(registrationService.sendPhoneRegistration(userPrincipal.getUser()));
    }

    @Operation(summary = "Check security code and do status verification user")
@Secured("ROLE_NOVERY")
    @GetMapping(value = "/phone/check/{code}")
    public ResponseEntity<String> checkPhoneCode(@RequestHeader("Authorization") String jwtToken,
             @PathVariable @Parameter(description = "Code for check phone") Long code) {
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService
                .loadUserByUsername(jwtTokenProvider.getPhoneEmail(jwtToken));
        return ResponseEntity.ok(registrationService.checkPhoneRegistration(userPrincipal.getUser(), code));
    }
}
