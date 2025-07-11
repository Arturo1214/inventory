package com.example.inventory_service.web.rest;

import com.example.inventory_service.domain.Product;
import com.example.inventory_service.service.ProductService;
import com.example.inventory_service.service.dto.CreateProductDto;
import com.example.inventory_service.service.errors.ProductNotFound;
import com.example.inventory_service.web.rest.errors.BaseAlertException;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Status;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public Page<Product> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @ParameterObject Pageable pageable) {
        BigDecimal min = minPrice == null ? BigDecimal.ZERO : minPrice;
        BigDecimal max = maxPrice == null ? BigDecimal.valueOf(Double.MAX_VALUE) : maxPrice;
        return productService.findByFilter(name, min, max, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody CreateProductDto dto) {
        try {
            return ResponseEntity.ok(productService.update(id, dto));
        } catch (ProductNotFound e) {
            throw new BaseAlertException("PRODUCT_NOT_FOUND", e.getMessage(), Status.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
