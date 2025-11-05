package com.awesome.booking.pizza.api.v1.controller;

import com.awesome.booking.pizza.api.v1.BaseSpringTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for Orders API using MockMvc.
 */
public class OrdersApiControllerIT extends BaseSpringTest {

    @Test
    public void shouldReturnBadRequestWhenPizzaNotFound() throws Exception {
        var requestBody = """
        {
          "items": [
            { "pizzaId": 999, "quantity": 1 }
          ]
        }
        """;

        mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateAndGetOrderByCode() throws Exception {
        var code = createOrder();

        mvc.perform(get("/api/v1/orders/" + code)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    public void shouldReturnNotFoundForInvalidCode() throws Exception {
        mvc.perform(get("/api/v1/orders/INVALID")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldListOrdersWithDefaultPagination() throws Exception {
        createOrder();
        createOrder();

        mvc.perform(get("/api/v1/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldListOrdersWithCustomPagination() throws Exception {
        createOrder();
        createOrder();
        createOrder();

        mvc.perform(get("/api/v1/orders?page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldListOrdersWithSorting() throws Exception {
        createOrder();
        createOrder();

        mvc.perform(get("/api/v1/orders?sortBy=createdAt&direction=desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void shouldTakeNextOrderSuccessfully() throws Exception {
        createOrder();

        mvc.perform(patch("/api/v1/orders/next")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void shouldReturnConflictWhenAnotherOrderInProgress() throws Exception {
        createOrder();
        takeNextOrder();
        createOrder();

        mvc.perform(patch("/api/v1/orders/next")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnNotFoundWhenNoOrdersAvailable() throws Exception {
        mvc.perform(patch("/api/v1/orders/next")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCompleteCurrentOrder() throws Exception {
        createOrder();
        takeNextOrder();

        mvc.perform(patch("/api/v1/orders/complete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    public void shouldReturnNotFoundWhenNoOrderInProgress() throws Exception {
        mvc.perform(patch("/api/v1/orders/complete")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldHandleFullOrderLifecycle() throws Exception {
        var code = createOrder();

        mvc.perform(get("/api/v1/orders/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));

        takeNextOrder();

        mvc.perform(get("/api/v1/orders/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mvc.perform(patch("/api/v1/orders/complete"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/orders/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    private String createOrder() throws Exception {
        var requestBody = """
        {
          "items": [
            { "pizzaId": 1, "quantity": 1 }
          ]
        }
        """;

        return mvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"code\":\"(.*?)\".*", "$1");
    }

    private void takeNextOrder() throws Exception {
        mvc.perform(patch("/api/v1/orders/next"));
    }
}
