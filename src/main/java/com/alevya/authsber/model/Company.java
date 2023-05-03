package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "company")
//do soft delete
@SQLDelete(sql = "update company set deleted = true where id =?")
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

    @ManyToOne
    @JoinColumn(name = "parent_division_id")/*(fetch = FetchType.EAGER)*/
    private Company parentDivision;
    @JsonIgnore
    @Singular
    @OneToMany(mappedBy = "parentDivision", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Company> childDivisions = new HashSet<>();

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent")
    private Set<Workplace> workplaces = new HashSet<>();

    /**
     * Soft delete: true - deleted, false - not deleted.
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean deleted = false;

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
