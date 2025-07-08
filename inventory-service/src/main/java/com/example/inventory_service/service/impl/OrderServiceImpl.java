package com.example.inventory_service.service.impl;

import com.example.inventory_service.domain.Order;
import com.example.inventory_service.domain.OrderItem;
import com.example.inventory_service.repository.OrderRepository;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.service.OrderService;
import com.example.inventory_service.service.StockValidator;
import com.example.inventory_service.service.dto.CreateOrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final StockValidator validator;

    public OrderServiceImpl(OrderRepository orderRepo, ProductRepository productRepo, StockValidator validator) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.validator = validator;
    }

    @Override
    public Order createOrder(CreateOrderDto dto) {
        Order order = new Order();
        order.setItems(dto.getItems().stream().map(i -> {
            var prod = productRepo.findById(i.getProductId()).orElseThrow();
            validator.validate(prod, i.getQuantity());
            prod.setStock(prod.getStock() - i.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(prod);
            item.setQuantity(i.getQuantity());
            item.setUnitPrice(prod.getPrice());
            return item;
        }).collect(Collectors.toList()));

        return orderRepo.save(order);
    }
}
