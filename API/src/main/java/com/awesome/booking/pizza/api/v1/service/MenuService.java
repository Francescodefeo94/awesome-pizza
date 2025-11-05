package com.awesome.booking.pizza.api.v1.service;

import com.awesome.booking.pizza.lib.PizzaResponse;
import org.springframework.data.domain.Page;

public interface MenuService {
    Page<PizzaResponse> getMenu(String name, Double maxPrice, String keyword,
                                int page, int size, String sortBy, String direction);
}
