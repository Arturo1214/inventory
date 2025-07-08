package com.example.inventory_service.service;

import com.example.inventory_service.domain.Order;
import com.example.inventory_service.service.dto.CreateOrderDto;
import com.example.inventory_service.service.errors.ProductNotFound;
import com.example.inventory_service.service.errors.StockError;

public interface OrderService {
    Order createOrder(CreateOrderDto dto) throws ProductNotFound, StockError;
}
