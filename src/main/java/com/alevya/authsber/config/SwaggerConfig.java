package com.alevya.authsber.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(name = "JWT Authentication"
        , scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT")
public class SwaggerConfig {
    //url
    //http://localhost:8080/swagger-ui/index.html
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Auth app for Sber")
                                .version("2.0.0")
                                .contact(
                                        new Contact()
                                                .email("privet49069@gmail.com")
                                                .name("Alexandra Sakharova")
                                )
                );
    }
}
