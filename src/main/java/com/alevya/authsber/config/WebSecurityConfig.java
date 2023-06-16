package com.alevya.authsber.config;

import com.alevya.authsber.security.JwtConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtConfigurer jwtConfigurer;

    public WebSecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web)  {
        web.ignoring().antMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**"
//                "/**"
        );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/v1/registration", "/api/v1/auth/login", "/swagger-ui").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout().logoutUrl("/api/v1/auth/logout").permitAll()
                .logoutSuccessUrl("/")
                .and()
                .apply(jwtConfigurer);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
