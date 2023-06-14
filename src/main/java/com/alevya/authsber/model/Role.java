package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_role")
@Getter
@Setter
public final class Role {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer level;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roles")
    @Singular
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "t_role_permission",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
    @Singular
    private Set<Permission> permissions = new HashSet<>();

    public Role() {
    }

    @Builder
    public Role(Long id,
                String name,
                String description,
                Integer level) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
    }

    @Builder
    public Role(Long id,
                String name,
                String description,
                Integer level,
                Set<User> users,
                Set<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.users = users;
        this.permissions = permissions;
    }

    public boolean addUser(User user) {
        return users.add(user);
    }

    public boolean removeUser(User user) {
        return users.remove(user);
    }

    public boolean addPermission(Permission permission) {
        return permissions.add(permission);
    }

    public boolean removePermission(Permission permission) {
        return permissions.remove(permission);
    }

    public void setName(String name) {
//        if (this.name == null || Objects.equals(this.name, name)) {
            this.name = name;
//        } else throw new BadRequestException("name cannot be changed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        Role role = (Role) o;

        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                '}';
    }
}
