package com.alevya.authsber.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@ToString
public class UserGeneralInfoDtoRequest {
    private String name;
    private String surname;
    private String patronymic;
    @Setter
    private String password; //todo
    private String email;
    private String phone;
    private LocalDate birthday;
    private String address;


}
