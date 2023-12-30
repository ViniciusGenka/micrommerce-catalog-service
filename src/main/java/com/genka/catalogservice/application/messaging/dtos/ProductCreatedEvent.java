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
public class ProductCreatedEvent {
    private UUID productId;
    private String name;
    private String description;
    private BigDecimal price;

    public static ProductCreatedEvent mapFromEntity(Product product) {
        return ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
