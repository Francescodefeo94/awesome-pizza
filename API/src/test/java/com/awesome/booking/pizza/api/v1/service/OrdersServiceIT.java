package com.awesome.booking.pizza.api.v1.service;

import com.awesome.booking.pizza.api.v1.BaseSpringTest;
import com.awesome.booking.pizza.lib.CreateOrderItemRequest;
import com.awesome.booking.pizza.lib.CreateOrderRequest;
import com.awesome.booking.pizza.lib.OrderResponse;
import com.awesome.booking.pizza.lib.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class OrdersServiceIT extends BaseSpringTest {

    @Autowired
    private OrderService orderService;

    private final Integer margheritaId = 1;
    private final Integer diavolaId = 2;


    @Test
    @DisplayName("createOrder: crea ordine con items e stato CREATED")
    void shouldCreateOrder() {
        var req = buildRequest(margheritaId, diavolaId);
        OrderResponse resp = orderService.createOrder(req);

        assertThat(resp.getCode()).isNotBlank();
        assertThat(resp.getStatus()).isEqualTo(OrderStatusEnum.CREATED);
        assertThat(resp.getItems()).hasSize(2);
        assertThat(orderRepository.findByCode(resp.getCode())).isPresent();
    }

    @Test
    @DisplayName("listOrders: restituisce pagina con sorting ASC per createdAt (default sortBy adattare)")
    void shouldListOrdersPaged() {
        orderService.createOrder(buildRequest(margheritaId));
        orderService.createOrder(buildRequest(diavolaId));

        var page = orderService.listOrders(0, 10, "createdAt", "ASC");

        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        assertThat(page.getContent()).allMatch(o -> o.getStatus() == OrderStatusEnum.CREATED);
    }

    @Test
    @DisplayName("getOrder: recupera ordine per code")
    void shouldRetrieveOrderByCode() {
        var created = orderService.createOrder(buildRequest(margheritaId));
        var fetched = orderService.getOrder(created.getCode());

        assertThat(fetched.getCode()).isEqualTo(created.getCode());
        assertThat(fetched.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("takeNextOrder: cambia stato CREATED -> IN_PROGRESS")
    public void shouldTakeNextOrder() {
        var created1 = orderService.createOrder(buildRequest(margheritaId));
        var created2 = orderService.createOrder(buildRequest(diavolaId));

        var taken = orderService.takeNextOrder();

        assertThat(taken.getStatus()).isEqualTo(OrderStatusEnum.IN_PROGRESS);
        assertThat(taken.getCode()).isIn(created1.getCode(), created2.getCode());
        assertThat(orderRepository.existsByStatus(OrderStatusEnum.IN_PROGRESS)).isTrue();
    }

    @Test
    @DisplayName("completeCurrentOrder: IN_PROGRESS -> COMPLETED")
    public void shouldCompleteCurrentOrder() {
        orderService.createOrder(buildRequest(margheritaId));
        var taken = orderService.takeNextOrder();

        var completed = orderService.completeCurrentOrder();

        assertThat(completed.getStatus()).isEqualTo(OrderStatusEnum.COMPLETED);
        assertThat(completed.getCode()).isEqualTo(taken.getCode());
    }


    private CreateOrderRequest buildRequest(Integer... pizzaIds) {
        var req = new CreateOrderRequest();
        req.setItems(
                Stream.of(pizzaIds)
                        .map(id -> {
                            var item = new CreateOrderItemRequest();
                            item.setPizzaId(id);
                            item.setQuantity(1);
                            return item;
                        })
                        .toList()
        );
        return req;
    }
}
