package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_permission")
@Getter
@Setter
public final class Permission {

    @Id
    @SequenceGenerator(name = "permission_seq", sequenceName = "permission_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="permission_seq")
    private Long id;

    @Column(nullable=false, unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("permissions")
    @Singular
    private Set<Role> roles = new HashSet<>();

    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    @Builder
    public Permission(Long id,
                      String name,
                      String description,
                      Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.roles = roles;
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean removeRole(Role role) {
        return roles.remove(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;

        Permission permission = (Permission) o;

        return Objects.equals(name, permission.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
