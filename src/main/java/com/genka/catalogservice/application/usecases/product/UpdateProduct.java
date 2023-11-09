package com.genka.catalogservice.application.usecases.product;

import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;

import java.util.UUID;

public interface UpdateProduct {
    ProductDTO execute(UUID productId, UpdateProductInput updateProductInput);
}
