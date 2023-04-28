package com.alevya.authsber.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //all path server
        registry.addMapping("/**")
                //open for host: localhost and port: 80
                .allowedOrigins("http://localhost")
                //all method
                .allowedMethods("*");
    }

}
