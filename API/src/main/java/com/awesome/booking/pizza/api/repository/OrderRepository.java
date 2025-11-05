package com.awesome.booking.pizza.api.repository;

import com.awesome.booking.pizza.api.entity.OrderEntity;
import com.awesome.booking.pizza.lib.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByCode(String code);

    boolean existsByStatus(OrderStatusEnum status);

    Optional<OrderEntity> findByStatus(OrderStatusEnum status);

    Optional<OrderEntity> findTopByStatusOrderByStatusAsc(OrderStatusEnum status);
}
