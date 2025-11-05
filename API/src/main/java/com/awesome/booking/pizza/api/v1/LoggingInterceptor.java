package com.awesome.booking.pizza.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        request.setAttribute("startTime", Instant.now());
        log.info("Incoming request: {} {} from {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        Instant startTime = (Instant) request.getAttribute("startTime");
        long duration = startTime != null ? Instant.now().toEpochMilli() - startTime.toEpochMilli() : -1;

        int status = response.getStatus();
        HttpStatus httpStatus = HttpStatus.resolve(status);

        if (httpStatus != null && httpStatus.is2xxSuccessful()) {
            log.info("Completed: {} {} {} in {}ms", request.getMethod(), request.getRequestURI(), status, duration);
        } else if (httpStatus != null && httpStatus.is4xxClientError()) {
            log.warn("Client error: {} {} {} in {}ms", request.getMethod(), request.getRequestURI(), status, duration);
        } else if (httpStatus != null && httpStatus.is5xxServerError()) {
            log.error("Server error: {} {} {} in {}ms", request.getMethod(), request.getRequestURI(), status, duration);
        } else {
            log.info("Completed: {} {} {} in {}ms", request.getMethod(), request.getRequestURI(), status, duration);
        }
    }
}
