package com.alevya.authsber.model;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "workplace")
    private Set<WorkTime> workTimes = new HashSet<>();

    @JsonIgnore
    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "workplace")
    private Set<Order> orders = new HashSet<>();

    public Workplace(String name,
                     String description,
                     Branch branch) {
        this.name = name;
        this.description = description;
        this.branch = branch;
    }

    public Workplace(Long id,
                     String name,
                     String description,
                     Branch branch) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branch = branch;
    }

    public Workplace(
            Long id,
            String name,
            String description,
            Branch branch,
            HashSet<WorkTime> workTimes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branch = branch;
        this.workTimes = workTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workplace)) return false;

        Workplace workplace = (Workplace) o;

        if (!Objects.equals(name, workplace.name)) return false;
        return Objects.equals(branch, workplace.branch);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (branch != null ? branch.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Workplace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", branchId=" + branch.getId() +
                '}';
    }
}
