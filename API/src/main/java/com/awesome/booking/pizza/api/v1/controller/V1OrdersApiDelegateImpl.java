package com.awesome.booking.pizza.api.v1.controller;

import com.awesome.booking.pizza.api.V1OrdersApiDelegate;
import com.awesome.booking.pizza.api.v1.service.OrderService;
import com.awesome.booking.pizza.lib.CreateOrderRequest;
import com.awesome.booking.pizza.lib.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.awesome.booking.pizza.api.util.StringUtils.*;

@Service
@RequiredArgsConstructor
public class V1OrdersApiDelegateImpl implements V1OrdersApiDelegate {

    private final OrderService orderService;

    @Override
    public ResponseEntity<OrderResponse> createOrder(CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderByCode(String code) {
        OrderResponse response = orderService.getOrder(code);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<OrderResponse>> listOrders( Integer page, Integer size, String sortBy, String direction) {
        Page<OrderResponse> orderResponses = orderService.listOrders(
                page == null ? 0 : page,
                size == null ? 10 : size,
                sortBy == null ? CREATED_AT : sortBy,
                direction == null ? ASC : direction
        );

        return ResponseEntity.ok()
                .header(TOTAL_COUNT, String.valueOf(orderResponses.getTotalElements()))
                .header(TOTAL_PAGES, String.valueOf(orderResponses.getTotalPages()))
                .body(orderResponses.getContent());
    }

    @Override
    public ResponseEntity<OrderResponse> takeNextOrder() {
        try {
            OrderResponse response = orderService.takeNextOrder();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    @Override
    public ResponseEntity<OrderResponse> completeCurrentOrder() {
        try {
            OrderResponse response = orderService.completeCurrentOrder();
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
