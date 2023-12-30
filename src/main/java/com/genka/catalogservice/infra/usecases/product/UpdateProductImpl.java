package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.services.PublishProductInventoryUpdatedEventService;
import com.genka.catalogservice.application.services.PublishProductUpdatedEventService;
import com.genka.catalogservice.application.usecases.product.UpdateProduct;
import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateProductImpl implements UpdateProduct {

    private final ProductDatabaseGateway productDatabaseGateway;
    private final PublishProductUpdatedEventService publishProductUpdatedEventService;
    private final PublishProductInventoryUpdatedEventService publishProductInventoryUpdatedEventService;
    private final ProductMapper productMapper;
    private final ModelMapper modelMapper;

    public UpdateProductImpl(ProductDatabaseGateway productDatabaseGateway, PublishProductUpdatedEventService publishProductUpdatedEventService, PublishProductInventoryUpdatedEventService publishProductInventoryUpdatedEventService, ProductMapper productMapper, ModelMapper modelMapper) {
        this.productDatabaseGateway = productDatabaseGateway;
        this.publishProductUpdatedEventService = publishProductUpdatedEventService;
        this.publishProductInventoryUpdatedEventService = publishProductInventoryUpdatedEventService;
        this.productMapper = productMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    @CachePut(value = "catalog", key = "#productId")
    public ProductDTO execute(UUID productId, UpdateProductInput updateProductInput) {
        Product product = this.productDatabaseGateway.findProductById(productId).orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
        this.modelMapper.map(updateProductInput, product);
        this.productDatabaseGateway.saveProduct(product);
        if(updateProductInput.getName() != null || updateProductInput.getDescription() != null || updateProductInput.getPrice() != null) {
            this.publishProductUpdatedEventService.execute(product);
        }
        if(updateProductInput.getStockQuantity() != null) {
            this.publishProductInventoryUpdatedEventService.execute(productId, updateProductInput.getStockQuantity());
        }
        return this.productMapper.mapEntityToDTO(product);
    }
}
