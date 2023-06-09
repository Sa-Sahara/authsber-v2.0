package com.alevya.authsber.controller;

import com.alevya.authsber.dto.AuthDtoRequest;
import com.alevya.authsber.dto.AuthDtoResponse;
import com.alevya.authsber.model.User;
import com.alevya.authsber.security.JwtTokenProvider;
import com.alevya.authsber.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "http://localhost:3000")
@Tag(name = "Auth Controller")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController{
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
             UserService userService,
             JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthDtoResponse> authenticate(@RequestBody AuthDtoRequest request) {
        try {
            UsernamePasswordAuthenticationToken up = new UsernamePasswordAuthenticationToken(request.getPhoneEmail(),
                    request.getPassword());
            authenticationManager.authenticate(up);
            User user = userService.getUserByPhoneEmail(request.getPhoneEmail());
            String token = jwtTokenProvider.createToken(request.getPhoneEmail(), user);
            AuthDtoResponse response = new AuthDtoResponse(token, null);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new AuthDtoResponse(null, "Invalid credentials"),
                    HttpStatus.FORBIDDEN);
        }
    }
}
