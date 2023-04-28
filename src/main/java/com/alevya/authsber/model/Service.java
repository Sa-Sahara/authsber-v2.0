package com.alevya.authsber.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public final class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
