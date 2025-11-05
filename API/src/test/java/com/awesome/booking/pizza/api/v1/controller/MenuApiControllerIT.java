package com.awesome.booking.pizza.api.v1.controller;

import com.awesome.booking.pizza.api.v1.BaseSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MenuApiControllerIT extends BaseSpringTest {

    @Test
    public void shouldReturnAllPizzasWithDefaultPagination() throws Exception {
        mvc.perform(get("/api/v1/menu")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Margherita"))
                .andExpect(jsonPath("$[0].price").value(8.00));
    }

    @Test
    public void shouldReturnCompleteFieldsForEachPizza() throws Exception {
        mvc.perform(get("/api/v1/menu")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].price").value(everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].name").value(everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].description").value(everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].image").value(everyItem(notNullValue())))
                .andExpect(jsonPath("$[*].id").value(everyItem(notNullValue())));
    }

    @Test
    public void shouldFilterPizzasByName() throws Exception {
        mvc.perform(get("/api/v1/menu?name=margherita")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Margherita"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void shouldFilterPizzasByMaxPrice() throws Exception {
        mvc.perform(get("/api/v1/menu?maxPrice=9.00")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(8.00))
                .andExpect(jsonPath("$[*].price").value(everyItem(
                        lessThanOrEqualTo(9.0))));
    }

    @Test
    public void shouldFilterPizzasByKeywordInDescription() throws Exception {
        mvc.perform(get("/api/v1/menu?keyword=mozzarella")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void shouldCombineMultipleFilters() throws Exception {
        mvc.perform(get("/api/v1/menu?name=diavola&maxPrice=10.00")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Diavola"))
                .andExpect(jsonPath("$[0].price").value(lessThanOrEqualTo(10.0)));
    }

    @Test
    public void shouldReturnEmptyListWhenNoMatch() throws Exception {
        mvc.perform(get("/api/v1/menu?name=nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void shouldApplyCustomPagination() throws Exception {
        mvc.perform(get("/api/v1/menu?page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldSortByPriceAscending() throws Exception {
        mvc.perform(get("/api/v1/menu?sortBy=price&direction=asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(lessThanOrEqualTo(
                        Double.parseDouble(mvc.perform(get("/api/v1/menu?sortBy=price&direction=asc"))
                                .andReturn().getResponse().getContentAsString()
                                .replaceAll(".*\"price\":(\\d+\\.\\d+).*", "$1")))));
    }

    @Test
    public void shouldSortByPriceDescending() throws Exception {
        mvc.perform(get("/api/v1/menu?sortBy=price&direction=desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void shouldSortByNameAscending() throws Exception {
        mvc.perform(get("/api/v1/menu?sortBy=name&direction=asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    public void shouldHandleInvalidPageNumber() throws Exception {
        mvc.perform(get("/api/v1/menu?page=999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void shouldReturnCorrectTotalCountHeader() throws Exception {
        mvc.perform(get("/api/v1/menu")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().string("X-Total-Count", matchesPattern("\\d+")));
    }

    @Test
    public void shouldReturnCorrectTotalPagesHeader() throws Exception {
        mvc.perform(get("/api/v1/menu?size=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(header().string("X-Total-Pages", matchesPattern("\\d+")));
    }

    @Test
    public void shouldApplyAllParametersTogether() throws Exception {
        mvc.perform(get("/api/v1/menu?name=margherita&maxPrice=10.00&page=0&size=5&sortBy=price&direction=asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}