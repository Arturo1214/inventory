package com.example.inventory_service.service.impl;

import com.example.inventory_service.domain.Product;
import com.example.inventory_service.service.StockValidator;
import com.example.inventory_service.service.errors.StockError;
import org.springframework.stereotype.Component;

@Component
public class DefaultStockValidator implements StockValidator {

    @Override
    public void validate(Product product, int quantity) throws StockError {
        if (product.getStock() < quantity) {
            throw new StockError(product);
        }
    }
}
