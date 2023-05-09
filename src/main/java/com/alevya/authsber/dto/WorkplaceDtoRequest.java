package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class WorkplaceDtoRequest {
    private String name;
    private String description;
    private Long companyId;
}
