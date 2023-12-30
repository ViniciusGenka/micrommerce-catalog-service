package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.services.PublishProductCreatedEventService;
import com.genka.catalogservice.application.usecases.product.AddProduct;
import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import com.genka.catalogservice.infra.services.PublishProductCreatedEventServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddProductImpl implements AddProduct {

    private final ProductDatabaseGateway productDatabaseGateway;
    private final PublishProductCreatedEventService publishProductCreatedService;
    private final ProductMapper productMapper;

    public AddProductImpl(ProductDatabaseGateway productDatabaseGateway, PublishProductCreatedEventServiceImpl publishProductCreatedService, ProductMapper productMapper) {
        this.productDatabaseGateway = productDatabaseGateway;
        this.publishProductCreatedService = publishProductCreatedService;
        this.productMapper = productMapper;
    }

    @Override
    public ProductDTO execute(AddProductInput addProductInput) {
        Product product = Product.builder()
                .name(addProductInput.getName())
                .description(addProductInput.getDescription())
                .price(addProductInput.getPrice())
                .build();
        Product savedProduct = this.productDatabaseGateway.saveProduct(product);
        this.publishProductCreatedService.execute(savedProduct);
        return this.productMapper.mapEntityToDTO(savedProduct);
    }
}
