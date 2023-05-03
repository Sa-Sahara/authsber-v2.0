package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class CompanyDtoResponse {
    private Long id;
    private String fullName;
    private String shortName;
    private String description;
    private String address;
    private String phone;
}
