package com.example.inventory_service.service.impl;

import com.example.inventory_service.domain.Product;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.service.ProductService;
import com.example.inventory_service.service.dto.CreateProductDto;
import com.example.inventory_service.service.errors.ProductNotFound;
import com.example.inventory_service.service.specification.ProductSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product create(CreateProductDto dto) {
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        return repo.save(p);
    }

    @Override
    public Page<Product> findByFilter(String name,
                                      BigDecimal minPrice,
                                      BigDecimal maxPrice,
                                      Pageable pageable) {

        Specification<Product> nameSpec  = ProductSpecs.nameContains(name);
        Specification<Product> priceSpec = ProductSpecs.priceBetween(minPrice, maxPrice);
        Specification<Product> spec = nameSpec.and(priceSpec);

        return repo.findAll(spec, pageable);
    }


    @Override
    public Product update(Long id, CreateProductDto dto) throws ProductNotFound {
        Product p = repo.findById(id).orElseThrow(ProductNotFound::new);
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        return repo.save(p);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
