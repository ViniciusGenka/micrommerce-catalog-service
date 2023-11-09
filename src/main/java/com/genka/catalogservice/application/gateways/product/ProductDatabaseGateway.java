package com.genka.catalogservice.application.gateways.product;

import com.genka.catalogservice.domain.product.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDatabaseGateway {
    Product saveProduct(Product product);

    Optional<Product> findProductById(UUID productId);

    List<Product> findAllProducts();
}
