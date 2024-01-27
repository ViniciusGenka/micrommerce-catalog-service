package com.genka.catalogservice.application.usecases.product.dtos;

import com.genka.catalogservice.infra.validations.PositiveAndInteger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotBlank(message = "name can not be blank")
    private String name;
    @NotBlank(message = "description can not be blank")
    private String description;
    @NotNull(message = "price is required")
    @Positive(message = "price must be positive")
    private BigDecimal price;
    @NotNull(message = "stockQuantity is required")
    @PositiveAndInteger(message = "stockQuantity must be positive and integer")
    private Integer stockQuantity;
}
