package com.example.inventory_service.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class OrderItemDto {

    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity;
}
