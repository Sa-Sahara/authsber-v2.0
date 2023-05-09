package com.alevya.authsber.controller;

import com.alevya.authsber.dto.AuthDtoRequest;
import com.alevya.authsber.dto.AuthDtoResponse;
import com.alevya.authsber.model.User;
import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Auth Controller")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController{
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Log log = LogFactory.getLog(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager
            , UserService userService
            , JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthDtoResponse> authenticate(@RequestBody AuthDtoRequest request) {
        try {
            UsernamePasswordAuthenticationToken up = new UsernamePasswordAuthenticationToken(request.getPhoneEmail()
                    , request.getPassword());
            authenticationManager.authenticate(up);
            User user = userService.getUserByPhoneEmail(request.getPhoneEmail());
            String token = jwtTokenProvider.createToken(request.getPhoneEmail(), user);
            AuthDtoResponse response = new AuthDtoResponse(token, null);
            log.info("login user id: " + user.getId() + " email: " + user.getEmail() + " phone: " + user.getPhone());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Invalid phone-email/password combination" + e.getMessage());
            return new ResponseEntity<>(new AuthDtoResponse(null, "Invalid phone-email/password combination")
                    , HttpStatus.FORBIDDEN);
        }
    }
}
