package com.example.inventory_service.service.errors;

public class ProductNotFound extends Exception {

    public ProductNotFound() {
        super("No existe el producto");
    }

    public ProductNotFound(Long id) {
        super("No existe el producto: " + id);
    }
}
