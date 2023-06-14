package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class PermissionDtoResponse {
    private Long id;
    private String name;
    private String description;
}
