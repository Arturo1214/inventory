package com.example.inventory_service.service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class CreateOrderDto {

    @NotEmpty
    private List<OrderItemDto> items;
}
