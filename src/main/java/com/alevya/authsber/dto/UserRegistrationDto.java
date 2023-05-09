package com.alevya.authsber.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter //todo: no setters
@ToString
public class UserRegistrationDto {
    private String name;
    private String surname;
    private String patronymic;
    private String password; //todo
    private String email;
    private String phone;
    private LocalDate birthday;
    private String address;
}
