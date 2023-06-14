package com.alevya.authsber.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@ToString
public class CompanyDtoRequest {
    private String fullName;
    private String shortName;
    private String description;
    private String address;
    private String phone;
    private Long parentCompanyId;
}
