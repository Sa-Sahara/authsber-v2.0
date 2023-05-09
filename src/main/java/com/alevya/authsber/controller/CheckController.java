package com.alevya.authsber.controller;

import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.security.UserDetailsServiceImpl;
import com.alevya.authsber.security.UserPrincipal;
import com.alevya.authsber.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for receiving main data
 */
@Hidden
@RestController
//@SecurityRequirement(name = "JWT Authentication")
@RequestMapping("/api/v1/check")
public class CheckController {

    private static final Log log = LogFactory.getLog(CheckController.class);
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;


    public CheckController(UserService userService
            , UserDetailsServiceImpl userDetailsService
            , JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Request for generate security code and send user by sms")
//    @Secured("ROLE_NOVERY")
    @GetMapping(value = "/phone/send")
    public ResponseEntity<String> sendPhoneCode(@RequestHeader("Authorization") String jwtToken) {
        log.info("checkPhone JWT: " + jwtToken);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService
                .loadUserByUsername(jwtTokenProvider.getPhoneEmail(jwtToken));
        return ResponseEntity.ok(userService.sendPhoneRegistration(userPrincipal.getUser()));
    }

    @Operation(summary = "Check security code and do status verification user")
//    @Secured("ROLE_NOVERY")
    @GetMapping(value = "/phone/check/{code}")
    public ResponseEntity<String> checkPhoneCode(@RequestHeader("Authorization") String jwtToken
            , @PathVariable @Parameter(description = "Code for check phone") String code) {
        log.info("checkPhone JWT: " + jwtToken);
        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService
                .loadUserByUsername(jwtTokenProvider.getPhoneEmail(jwtToken));
        return ResponseEntity.ok(userService.checkPhoneRegistration(userPrincipal.getUser(), code));
    }
}
