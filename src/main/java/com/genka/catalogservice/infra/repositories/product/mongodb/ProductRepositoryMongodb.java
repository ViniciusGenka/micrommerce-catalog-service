package com.genka.catalogservice.infra.repositories.product.mongodb;

import com.genka.catalogservice.domain.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ProductRepositoryMongodb extends MongoRepository<Product, UUID> {
}
