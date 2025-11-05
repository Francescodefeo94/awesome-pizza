package com.awesome.booking.pizza.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PizzaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column
    private String image;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String notes;
}
