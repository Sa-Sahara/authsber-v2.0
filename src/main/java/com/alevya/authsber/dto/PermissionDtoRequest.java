package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class PermissionDtoRequest {
    private String name;
    private String description;
//    private Set<Role> roles;
}
