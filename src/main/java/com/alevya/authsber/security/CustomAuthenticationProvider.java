package com.alevya.authsber.security;

import com.alevya.authsber.model.User;
import com.alevya.authsber.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomAuthenticationProvider(UserService userService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phoneEmail = authentication.getName();
        String password = authentication.getCredentials().toString();
        //get user
        User user = userService.getUserByPhoneEmail(phoneEmail);
        if (user == null) {
            throw new BadCredentialsException("Unknown user " + phoneEmail);
        }
//        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) { //TODO
        if (!password.equals(user.getPassword())) {
            throw new BadCredentialsException("Bad password");
        }
        UserDetails principal = new UserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(
                principal, password, principal.getAuthorities());
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
