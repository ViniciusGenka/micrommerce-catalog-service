package com.genka.catalogservice.application.messaging.dtos;

import com.genka.catalogservice.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInventoryUpdatedEvent {
    private UUID productId;
    private Integer stockQuantity;
}
