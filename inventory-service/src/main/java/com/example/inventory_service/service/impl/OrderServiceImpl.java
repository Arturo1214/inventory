package com.example.inventory_service.service.impl;

import com.example.inventory_service.domain.Order;
import com.example.inventory_service.domain.OrderItem;
import com.example.inventory_service.domain.Product;
import com.example.inventory_service.repository.OrderRepository;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.service.OrderService;
import com.example.inventory_service.service.StockValidator;
import com.example.inventory_service.service.dto.CreateOrderDto;
import com.example.inventory_service.service.errors.ProductNotFound;
import com.example.inventory_service.service.errors.StockError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public Order createOrder(CreateOrderDto dto) throws ProductNotFound, StockError {
        Order order = new Order();
        List<OrderItem> items = new ArrayList<>();

        for (var itemDto : dto.getItems()) {
            Product prod = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFound(itemDto.getProductId()));

            validator.validate(prod, itemDto.getQuantity());

            prod.setStock(prod.getStock() - itemDto.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(prod);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitPrice(prod.getPrice());

            items.add(item);
        }

        order.setItems(items);
        return orderRepo.save(order);
    }

}
