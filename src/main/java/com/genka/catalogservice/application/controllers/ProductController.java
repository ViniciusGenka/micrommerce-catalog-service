package com.genka.catalogservice.application.controllers;

import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ProductController {
    ResponseEntity<ProductDTO> addProduct(AddProductInput addProductInput);

    ResponseEntity<ProductDTO> updateProduct(UUID productId, UpdateProductInput updateProductInput);

    ResponseEntity<ProductDTO> getOneProduct(UUID productId);

    ResponseEntity<List<ProductDTO>> getAllProducts();


}
