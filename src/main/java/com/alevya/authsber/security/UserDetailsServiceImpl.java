package com.alevya.authsber.security;

import com.alevya.authsber.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneEmail) throws UsernameNotFoundException {
        if(phoneEmail.contains("@")){
            return new UserPrincipal(userRepository.findByEmail(phoneEmail));
        } else {
            return new UserPrincipal(userRepository.findByPhone(phoneEmail));
        }
    }
}
