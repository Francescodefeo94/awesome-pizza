package com.awesome.booking.pizza.api.v1.service;

import com.awesome.booking.pizza.lib.CreateOrderRequest;
import com.awesome.booking.pizza.lib.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    Page<OrderResponse> listOrders(int page, int size, String sortBy, String direction);

    OrderResponse getOrder(String code);

    OrderResponse takeNextOrder();

    OrderResponse completeCurrentOrder();
}
