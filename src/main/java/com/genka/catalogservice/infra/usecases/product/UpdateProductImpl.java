package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.messaging.MessagePublisher;
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
    private final MessagePublisher messagePublisher;
    private final ProductMapper productMapperMock;
    private final ModelMapper modelMapper;

    public UpdateProductImpl(ProductDatabaseGateway productDatabaseGateway, MessagePublisher messagePublisher, ProductMapper productMapperMock, ModelMapper modelMapper) {
        this.productDatabaseGateway = productDatabaseGateway;
        this.messagePublisher = messagePublisher;
        this.productMapperMock = productMapperMock;
        this.modelMapper = modelMapper;
    }

    @Override
    @CachePut(value = "catalog", key = "#productId")
    public ProductDTO execute(UUID productId, UpdateProductInput updateProductInput) {
        Product product = this.productDatabaseGateway.findProductById(productId).orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
        this.modelMapper.map(updateProductInput, product);
        this.productDatabaseGateway.saveProduct(product);
        this.messagePublisher.sendMessage(
                "product_updated",
                "{\"" +
                        "id\": \"" + product.getId() +
                        "name\": \"" + product.getName() +
                        "description\": \"" + product.getDescription() +
                        "price\": \"" + product.getPrice() +
                        "\", \"stockQuantity\": " + updateProductInput.getStockQuantity() +
                        "}"
        );
        return this.productMapperMock.mapEntityToDTO(product);
    }
}
