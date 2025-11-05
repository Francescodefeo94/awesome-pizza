package com.awesome.booking.pizza.api.mapper;

import com.awesome.booking.pizza.api.entity.PizzaEntity;
import com.awesome.booking.pizza.lib.PizzaResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    PizzaResponse toDto(PizzaEntity entity);
}
