package com.alevya.authsber.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@ToString
public class UserWithSettingsDtoResponse extends UserGeneralInfoDtoResponse {
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean blocked;
    @Singular
    private Set<String> roleNames = new HashSet<>();
    private Date createDate;
    private Date modifyDate;
    private Boolean deleted;

    public boolean addRole(String roleName) {
        return roleNames.add(roleName);
    }

    public boolean removeRole(String roleName) {
        return roleNames.remove(roleName);
    }
}
