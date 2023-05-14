package com.alevya.authsber.model;

import com.alevya.authsber.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_company")
//do soft delete
@SQLDelete(sql = "update t_company set deleted = true where id =?")
@Getter
@Setter
public final class Company {

    @Id
    @SequenceGenerator(name = "company_seq", sequenceName = "company_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    private Long id;

    //    should not be changed
    @Column(name = "full_name", unique = true)
    @NotNull
    private String fullName;

    @Column(name = "short_name", unique = true)
    @NotNull
    private String shortName;

    private String description;

    @NotNull
    private String address;

    private String phone;

    @ManyToOne
    @JoinColumn(name = "parent_division_id")/*(fetch = FetchType.EAGER)*/
    private Company parentCompany;
    @JsonIgnore
    @Singular
    @OneToMany(mappedBy = "parentCompany", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Company> childCompanies = new HashSet<>();

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "company")
    private Set<Workplace> workplaces = new HashSet<>();

    /**
     * Soft delete: true - deleted, false - not deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean deleted = false;

    public Company() {
    }

    @Builder
    public Company(String fullName,
                   String shortName,
                   String description,
                   String address,
                   String phone,
                   Company parentCompany) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.parentCompany = parentCompany;
    }

    @Builder
    public Company(Long id,
                   String fullName,
                   String shortName,
                   String description,
                   String address,
                   String phone,
                   Company parentCompany,
                   Set<Company> childCompanies,
                   Set<Workplace> workplaces,
                   Boolean deleted) {
        this.id = id;
        this.fullName = fullName;
        this.shortName = shortName;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.parentCompany = parentCompany;
        this.childCompanies = childCompanies;
        this.workplaces = workplaces;
        this.deleted = deleted;
    }

    public boolean addChildCompany(Company company) {
        if (!this.equals(company)) {
            return false;
        }
        return childCompanies.add(company);
    }

    public boolean removeChildCompany(Company company) {
        return childCompanies.remove(company);
    }

    public boolean addWorkplace(Workplace workplace) {
        return workplaces.add(workplace);
    }

    public boolean removeWorkplace(Workplace workplace) {
        return workplaces.remove(workplace);
    }

    public void setFullName(String fullName) {
        if (this.fullName == null || Objects.equals(this.fullName, fullName)) {
            this.fullName = fullName;
        } else throw new BadRequestException("fullName cannot be changed");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;

        Company company = (Company) o;

        return Objects.equals(fullName, company.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
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
