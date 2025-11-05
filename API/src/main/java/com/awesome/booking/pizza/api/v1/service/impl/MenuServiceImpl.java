package com.awesome.booking.pizza.api.v1.service.impl;

import com.awesome.booking.pizza.api.mapper.PizzaMapper;
import com.awesome.booking.pizza.api.repository.PizzaRepository;
import com.awesome.booking.pizza.api.specification.PizzaSpecification;
import com.awesome.booking.pizza.api.v1.service.MenuService;
import com.awesome.booking.pizza.lib.PizzaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import static com.awesome.booking.pizza.api.util.StringUtils.ASC;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final PizzaRepository pizzaRepository;
    private final PizzaMapper mapper;

    @Override
    public Page<PizzaResponse> getMenu(String name, Double maxPrice, String keyword,
                                       int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase(ASC)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return pizzaRepository.findAll(PizzaSpecification.build(name, maxPrice, keyword), pageable)
                .map(mapper::toDto);
    }
}
