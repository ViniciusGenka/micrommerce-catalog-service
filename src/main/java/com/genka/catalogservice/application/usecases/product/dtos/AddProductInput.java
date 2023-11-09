package com.genka.catalogservice.application.usecases.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddProductInput {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
}
