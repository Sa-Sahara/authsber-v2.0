package com.alevya.authsber.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "t_service")
@Getter
@Setter
public final class Service {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "service_seq", sequenceName = "service_seq", allocationSize = 1)
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="service_seq")
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Column(name = "length_slots")
    private int lengthSlots;

    private String description;

    @NotNull
    private int price;

    public Service() {
    }

    @Builder
    public Service(Long id,
                   String name,
                   int lengthSlots,
                   String description,
                   int price
                   ) {
        this.id = id;
        this.name = name;
        this.lengthSlots = lengthSlots;
        this.description = description;
        this.price = price;
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
