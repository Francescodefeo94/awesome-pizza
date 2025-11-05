package com.awesome.booking.pizza.api.mapper;

import com.awesome.booking.pizza.api.entity.OrderEntity;
import com.awesome.booking.pizza.lib.CreateOrderRequest;
import com.awesome.booking.pizza.lib.OrderResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PizzaMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderEntity toEntity(CreateOrderRequest request);

    @Mapping(target = "items", source = "items")
    OrderResponse toDto(OrderEntity entity);

    List<OrderResponse> toDtoList(List<OrderEntity> entities);
}
