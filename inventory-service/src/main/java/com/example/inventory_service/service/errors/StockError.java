package com.example.inventory_service.service.errors;

import com.example.inventory_service.domain.Product;

public class StockError extends Exception {

    public StockError(Product product) {
        super("Stock insuficiente para producto: " + product.getId());
    }
}
