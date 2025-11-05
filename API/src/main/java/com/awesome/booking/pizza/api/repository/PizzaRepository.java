package com.awesome.booking.pizza.api.repository;

import com.awesome.booking.pizza.api.entity.PizzaEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PizzaRepository extends JpaRepository<PizzaEntity, Integer>, JpaSpecificationExecutor<PizzaEntity> {
}
