package com.awesome.booking.pizza.api.specification;

import com.awesome.booking.pizza.api.entity.PizzaEntity;
import org.springframework.data.jpa.domain.Specification;

public class PizzaSpecification {

    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String NAME = "name";

    public static Specification<PizzaEntity> hasName(String name) {
        return (root, query, cb) -> name == null || name.isBlank()
                ? null
                : cb.like(cb.lower(root.get(NAME)), "%" + name.toLowerCase() + "%");
    }

    public static Specification<PizzaEntity> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) -> maxPrice == null
                ? null
                : cb.lessThanOrEqualTo(root.get(PRICE), maxPrice);
    }

    public static Specification<PizzaEntity> hasKeyword(String keyword) {
        return (root, query, cb) -> keyword == null || keyword.isBlank()
                ? null
                : cb.like(cb.lower(root.get(DESCRIPTION)), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<PizzaEntity> build(String name, Double maxPrice, String keyword) {
        return Specification.allOf(hasName(name))
                .and(hasMaxPrice(maxPrice))
                .and(hasKeyword(keyword));
    }
}
