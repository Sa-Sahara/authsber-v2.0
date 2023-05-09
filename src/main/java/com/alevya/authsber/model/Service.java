package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "t_service")
@Getter
@Setter
public final class Service {

    @Id
    @SequenceGenerator(name = "service_seq", sequenceName = "service_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="service_seq")
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    private String description;

    @NotNull
    private int price;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("services")
    @Singular
    private Set<Order> orders = new HashSet<>();

    public Service() {
    }

    @Builder
    public Service(Long id,
                   String name,
                   String description,
                   int price,
                   Set<Order> orders) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Service service)) return false;
        return name.equals(service.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
