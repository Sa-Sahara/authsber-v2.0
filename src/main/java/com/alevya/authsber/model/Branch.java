package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "branch")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    private String description;

    private String address;

    private String phone;

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "branch")
    private Set<Workplace> workplaces = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "company_id")
    private Company company;


    public Branch(String name,
                  String description,
                  String address,
                  String phone,
                  Company company) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.company = company;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        Branch branch = (Branch) o;

        if (!Objects.equals(name, branch.name)) return false;
        return Objects.equals(company, branch.company);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (company != null ? company.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", companyId=" + company.getId() +
                '}';
    }
}
