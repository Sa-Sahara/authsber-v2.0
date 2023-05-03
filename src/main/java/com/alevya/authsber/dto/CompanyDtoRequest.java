package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class CompanyDtoRequest {
    private String fullName;
    private String shortName;
    private String description;
    private String address;
    private String phone;
}
