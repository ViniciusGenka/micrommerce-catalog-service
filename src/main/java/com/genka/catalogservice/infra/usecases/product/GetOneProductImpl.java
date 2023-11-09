package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.usecases.product.GetOneProduct;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetOneProductImpl implements GetOneProduct {

    private final ProductDatabaseGateway productDatabaseGateway;
    private final ProductMapper productMapper;

    public GetOneProductImpl(ProductDatabaseGateway productDatabaseGateway, ProductMapper productMapper) {
        this.productDatabaseGateway = productDatabaseGateway;
        this.productMapper = productMapper;
    }

    @Override
    @Cacheable(value = "catalog", key = "#productId")
    public ProductDTO execute(UUID productId) {
        Product product = this.productDatabaseGateway.findProductById(productId).orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
        return this.productMapper.mapEntityToDTO(product);
    }
}
