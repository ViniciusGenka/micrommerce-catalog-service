package com.genka.catalogservice.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.UUID;

@Document(value = "products")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {
    @MongoId
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
}
