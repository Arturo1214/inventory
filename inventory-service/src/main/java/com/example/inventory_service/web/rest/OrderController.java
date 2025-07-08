package com.example.inventory_service.web.rest;

import com.example.inventory_service.domain.Order;
import com.example.inventory_service.service.OrderService;
import com.example.inventory_service.service.dto.CreateOrderDto;
import com.example.inventory_service.service.errors.ProductNotFound;
import com.example.inventory_service.service.errors.StockError;
import com.example.inventory_service.web.rest.errors.BaseAlertException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Status;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<Order> create(@Valid @RequestBody CreateOrderDto dto) {
        try {
            return ResponseEntity.ok(orderService.createOrder(dto));
        } catch (ProductNotFound e) {
            throw new BaseAlertException("PRODUCT_NOT_FOUND", e.getMessage(), Status.BAD_REQUEST);
        } catch (StockError e) {
            throw new BaseAlertException("STOCK_NOT_FOUND", e.getMessage(), Status.BAD_REQUEST);
        }
    }
}
