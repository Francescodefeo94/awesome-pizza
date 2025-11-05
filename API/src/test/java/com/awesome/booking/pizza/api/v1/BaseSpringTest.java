package com.awesome.booking.pizza.api.v1;

import com.awesome.booking.pizza.api.repository.OrderRepository;
import com.awesome.booking.pizza.api.repository.PizzaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for all integration tests.
 * Spins up PostgreSQL Testcontainer and bootstraps Spring Boot context.
 */
@SpringBootTest
@Slf4j
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
@Testcontainers
@Sql("/sql/initialization.sql")
@DirtiesContext
public abstract class BaseSpringTest {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected PizzaRepository pizzaRepository;
    @Autowired
    protected OrderRepository orderRepository;


    @Container
    static PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("awesome_pizza_test")
                    .withUsername("sa")
                    .withPassword("sa");


    @DynamicPropertySource
    static void registerDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeAll
    public static void startContainer() {
        POSTGRES.start();
    }

    @AfterEach
    public void tearDown() {
        pizzaRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterAll
    public static void stopContainer() {
        POSTGRES.stop();
    }
}
