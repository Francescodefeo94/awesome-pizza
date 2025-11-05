package com.awesome.booking.pizza.api.v1.service.impl;

import com.awesome.booking.pizza.api.entity.OrderPizzaEntity;
import com.awesome.booking.pizza.api.entity.PizzaEntity;
import com.awesome.booking.pizza.api.mapper.OrderMapper;
import com.awesome.booking.pizza.api.mapper.OrderPizzaMapper;
import com.awesome.booking.pizza.api.repository.OrderRepository;
import com.awesome.booking.pizza.api.repository.PizzaRepository;
import com.awesome.booking.pizza.api.v1.service.OrderService;
import com.awesome.booking.pizza.lib.CreateOrderRequest;
import com.awesome.booking.pizza.lib.OrderResponse;
import com.awesome.booking.pizza.lib.OrderStatusEnum;
import com.awesome.booking.pizza.lib.exception.ConflictException;
import com.awesome.booking.pizza.lib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.awesome.booking.pizza.api.util.StringUtils.ASC;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderPizzaMapper orderPizzaMapper;
    private final PizzaRepository pizzaRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        var order = orderMapper.toEntity(request);
        order.setCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setStatus(OrderStatusEnum.CREATED);

        List<OrderPizzaEntity> items = request.getItems().stream()
                .map(item -> {
                    PizzaEntity pizza = pizzaRepository.findById(item.getPizzaId())
                            .orElseThrow(() -> new ResourceNotFoundException("Pizza not found: " + item.getPizzaId()));

                    OrderPizzaEntity entity = orderPizzaMapper.toEntity(item);
                    entity.setPizza(pizza);
                    entity.setOrder(order);
                    return entity;
                })
                .toList();
        order.setItems(items);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> listOrders(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase(ASC)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public OrderResponse getOrder(String code) {
        var entity = orderRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine con codice " + code + " non trovato"));
        return orderMapper.toDto(entity);
    }

    @Override
    public OrderResponse takeNextOrder() {
        if (orderRepository.existsByStatus(OrderStatusEnum.IN_PROGRESS)) {
            throw new ConflictException("Esiste già un ordine in lavorazione");
        }
        var next = orderRepository.findTopByStatusOrderByStatusAsc(OrderStatusEnum.CREATED)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Nessun ordine in coda"));
        next.setStatus(OrderStatusEnum.IN_PROGRESS);
        return orderMapper.toDto(orderRepository.save(next));
    }

    @Override
    public OrderResponse completeCurrentOrder() {
        var order = orderRepository.findByStatus(OrderStatusEnum.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("Ordine non trovato"));

        order.setStatus(OrderStatusEnum.COMPLETED);

        System.out.println("Stato ordine " + order.getCode() + " completato");
        return orderMapper.toDto(orderRepository.save(order));
    }
}
