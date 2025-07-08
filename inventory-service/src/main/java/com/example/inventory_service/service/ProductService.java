package com.example.inventory_service.service;

import com.example.inventory_service.domain.Product;
import com.example.inventory_service.service.dto.CreateProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

public interface ProductService {
    Product create(CreateProductDto dto);
    Page<Product> findByFilter(String name, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Product update(Long id, CreateProductDto dto);
    void delete(Long id);
}
