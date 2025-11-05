package com.awesome.booking.pizza.api.v1.service;

import com.awesome.booking.pizza.api.entity.PizzaEntity;
import com.awesome.booking.pizza.api.specification.PizzaSpecification;
import com.awesome.booking.pizza.api.v1.BaseSpringTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;


import static org.assertj.core.api.Assertions.assertThat;

public class PizzaSpecificationIT extends BaseSpringTest {


    @Nested
    @DisplayName("Filter by name")
    public class NameFilterTests {

        @Test
        @DisplayName("should return pizzas matching partial name (case-insensitive)")
        public void testHasName() {
            var spec = PizzaSpecification.hasName("margherita");
            var result = pizzaRepository.findAll(spec);

            assertThat(result)
                    .hasSize(1)
                    .extracting(PizzaEntity::getName)
                    .containsExactly("Margherita");
        }

        @Test
        @DisplayName("should return empty list when no pizza matches name")
        public void testHasNameNoMatch() {
            var spec = PizzaSpecification.hasName("Hawaii");
            var result = pizzaRepository.findAll(spec);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should ignore null name filter")
        public void testHasNameNull() {
            var spec = PizzaSpecification.hasName(null);
            var result = pizzaRepository.findAll(spec);

            assertThat(result).hasSize(4);
        }
    }

    @Nested
    @DisplayName("Filter by max price")
    public class PriceFilterTests {

        @Test
        @DisplayName("should return pizzas cheaper than or equal to maxPrice")
        public void testHasMaxPrice() {
            var spec = PizzaSpecification.hasMaxPrice(9.00);
            var result = pizzaRepository.findAll(spec);

            assertThat(result)
                    .hasSize(2)
                    .extracting(PizzaEntity::getName)
                    .containsExactlyInAnyOrder("Margherita", "Diavola");
        }

        @Test
        @DisplayName("should ignore null maxPrice filter")
        public void testHasMaxPriceNull() {
            var spec = PizzaSpecification.hasMaxPrice(null);
            var result = pizzaRepository.findAll(spec);

            assertThat(result).hasSize(4);
        }
    }

    @Nested
    @DisplayName("Filter by keyword in description")
    public class KeywordFilterTests {

        @Test
        @DisplayName("should return pizzas containing keyword in description")
        public void testHasKeyword() {
            var spec = PizzaSpecification.hasKeyword("mozzarella");
            var result = pizzaRepository.findAll(spec);

            assertThat(result)
                    .hasSize(2)
                    .extracting(PizzaEntity::getName)
                    .containsExactlyInAnyOrder("Margherita", "Diavola");
        }

        @Test
        @DisplayName("should return empty when keyword not present")
        public void testHasKeywordNoMatch() {
            var spec = PizzaSpecification.hasKeyword("ananas");
            var result = pizzaRepository.findAll(spec);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should ignore null keyword filter")
        public void testHasKeywordNull() {
            var spec = PizzaSpecification.hasKeyword(null);
            var result = pizzaRepository.findAll(spec);

            assertThat(result).hasSize(4);
        }
    }

    @Nested
    @DisplayName("Combined filters")
    public class CombinedFiltersTests {

        @Test
        @DisplayName("should apply multiple filters together")
        public void testCombined() {
            Specification<PizzaEntity> spec = PizzaSpecification.build(
                    "a",                      // match name contains "a"
                    8.00, // max price 8
                    "pomodoro"                // description contains "pomodoro"
            );

            var result = pizzaRepository.findAll(spec, PageRequest.of(0, 10)).getContent();

            assertThat(result)
                    .hasSize(1)
                    .extracting(PizzaEntity::getName)
                    .containsExactly("Margherita");
        }
    }
}
