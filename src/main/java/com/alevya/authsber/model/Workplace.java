package com.alevya.authsber.model;

import com.alevya.authsber.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "workplace")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class Workplace {

    @Id
    @SequenceGenerator(name = "workplace_seq", sequenceName = "workplace_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="workplace_seq")
    private Long id;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "company_id")
    private Company company;

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "workplace")
    private Set<WorkTime> workTimes = new HashSet<>();

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "workplace")
    private Set<Order> orders = new HashSet<>();

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
        } else throw new BadRequestException("Name cannot be changed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        Workplace workplace = (Workplace) o;

        return Objects.equals(name, workplace.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Workplace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", companyId=" + company.getId() +
                '}';
    }
}
