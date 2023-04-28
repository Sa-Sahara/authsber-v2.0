package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "company")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", unique = true)
    @NotNull
    private String fullName;

    @Column(name = "short_name", unique = true)
    @NotNull
    private String shortName;

    private String description;

    private String address;

    private String phone;

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "company")
    private Set<Branch> branches = new HashSet<>();

    public Company(String fullName,
                   String shortName,
                   String description,
                   String address,
                   String phone) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;

        Company company = (Company) o;

        if (!Objects.equals(fullName, company.fullName)) return false;
        return Objects.equals(shortName, company.shortName);
    }

    @Override
    public int hashCode() {
        int result = fullName != null ? fullName.hashCode() : 0;
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", name='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
