package com.awesome.booking.pizza.api.v1.controller;

import com.awesome.booking.pizza.api.V1MenuApiDelegate;
import com.awesome.booking.pizza.api.v1.service.MenuService;
import com.awesome.booking.pizza.lib.PizzaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.awesome.booking.pizza.api.util.StringUtils.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class V1MenuApiDelegateImpl implements V1MenuApiDelegate {

    private final MenuService menuService;

    @Override
    public ResponseEntity<List<PizzaResponse>> getMenu(
            String name, Double maxPrice, String keyword,
            Integer page, Integer size, String sortBy, String direction) {

        Page<PizzaResponse> pizzas = menuService.getMenu(
                name, maxPrice, keyword,
                page == null ? 0 : page,
                size == null ? 10 : size,
                sortBy == null ? PRICE : sortBy,
                direction == null ? ASC : direction
        );

        return ResponseEntity.ok()
                .header(TOTAL_COUNT, String.valueOf(pizzas.getTotalElements()))
                .header(TOTAL_PAGES, String.valueOf(pizzas.getTotalPages()))
                .body(pizzas.getContent());
    }
}
