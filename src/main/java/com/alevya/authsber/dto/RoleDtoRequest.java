package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class RoleDtoRequest {
    private String name;
    private String description;
    private Integer level;
}
