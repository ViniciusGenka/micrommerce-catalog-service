package com.genka.catalogservice.application.usecases.product;

import com.genka.catalogservice.domain.product.dtos.ProductDTO;

import java.util.UUID;

public interface GetOneProduct {
    ProductDTO execute(UUID productId);
}
