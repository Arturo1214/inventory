package com.example.inventory_service.service;

import com.example.inventory_service.domain.Product;

public interface StockValidator {
    void validate(Product product, int quantity);
}
