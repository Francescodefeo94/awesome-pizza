package com.awesome.booking.pizza.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_pizzas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPizzaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_id", nullable = false)
    private PizzaEntity pizza;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private String notes;
}
