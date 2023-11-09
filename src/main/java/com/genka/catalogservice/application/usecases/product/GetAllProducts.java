package com.genka.catalogservice.application.usecases.product;

import com.genka.catalogservice.domain.product.dtos.ProductDTO;

import java.util.List;

public interface GetAllProducts {
    List<ProductDTO> execute();
}
