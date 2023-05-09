package com.alevya.authsber.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class RoleDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Integer level;
//    private Set<User> users;
//    private Set<Role> permissions;
}
