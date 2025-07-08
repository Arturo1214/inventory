package com.example.inventory_service.service;

import com.example.inventory_service.domain.Product;
import com.example.inventory_service.service.errors.StockError;

public interface StockValidator {
    void validate(Product product, int quantity) throws StockError;
}
