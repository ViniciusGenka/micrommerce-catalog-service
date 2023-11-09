package com.genka.catalogservice.infra.repositories.product.mongodb;

import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.domain.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductDatabaseGatewayMongodb implements ProductDatabaseGateway {
    private final ProductRepositoryMongodb productRepositoryMongodb;

    public ProductDatabaseGatewayMongodb(ProductRepositoryMongodb productRepositoryMongodb) {
        this.productRepositoryMongodb = productRepositoryMongodb;
    }

    @Override
    public Product saveProduct(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID());
        }
        return this.productRepositoryMongodb.save(product);
    }

    @Override
    public Optional<Product> findProductById(UUID productId) {
        return this.productRepositoryMongodb.findById(productId);
    }

    @Override
    public List<Product> findAllProducts() {
        return this.productRepositoryMongodb.findAll();

    }
}
