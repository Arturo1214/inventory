package com.example.inventory_service.service;

import com.example.inventory_service.domain.Order;
import com.example.inventory_service.service.dto.CreateOrderDto;

public interface OrderService {
    Order createOrder(CreateOrderDto dto);
}
