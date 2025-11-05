package com.awesome.booking.pizza.api.repository;

import com.awesome.booking.pizza.api.entity.OrderPizzaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPizzaRepository extends JpaRepository<OrderPizzaEntity, Long> {

}
