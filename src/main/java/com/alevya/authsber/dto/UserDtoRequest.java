package com.alevya.authsber.dto;

import com.alevya.authsber.model.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter //todo: no setters
@ToString
public class UserDtoRequest extends UserRegistrationDto{
//    private String name;
//    private String surname;
//    private String patronymic;
//    private String password;
//    private String email;
    private Boolean emailVerified;
//    private String phone;
    private Boolean phoneVerified;
//    private String address;
//    private LocalDate birthday;
    private Boolean blocked;
    private Set<Role> roles = new HashSet<>();
    private Date createDate;
    private Date modifyDate;
    private Boolean deleted;

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean removeRole(Role role) {
        return roles.remove(role);
    }
}
