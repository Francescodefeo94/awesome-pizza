package com.awesome.booking.pizza.api.mapper;

import com.awesome.booking.pizza.api.entity.OrderPizzaEntity;
import com.awesome.booking.pizza.lib.CreateOrderItemRequest;
import com.awesome.booking.pizza.lib.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PizzaMapper.class})
public interface OrderPizzaMapper {

    @Mapping(target = "pizza", source = "pizza")
    OrderItemResponse toDto(OrderPizzaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pizza", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderPizzaEntity toEntity(CreateOrderItemRequest dto);

    List<OrderItemResponse> toDtoList(List<OrderPizzaEntity> entities);
}
