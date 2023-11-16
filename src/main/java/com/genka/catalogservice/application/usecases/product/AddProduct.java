package com.genka.catalogservice.application.usecases.product;

import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;

public interface AddProduct {
    ProductDTO execute(AddProductInput addProductInput);
}
