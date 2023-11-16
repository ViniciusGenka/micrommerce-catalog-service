package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.messaging.MessagePublisher;
import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductImplTest {

    @Mock
    private ProductDatabaseGateway productDatabaseGatewayMock;
    @Mock
    private ProductMapper productMapperMock;
    @Spy
    private ModelMapper modelMapperSpy;
    @Mock
    private MessagePublisher messagePublisher;
    @InjectMocks
    private UpdateProductImpl sut;

    @Test
    @DisplayName("It should find the product, make the updates, save the changes, map the updated product to a ProductDTO and publish the ProductUpdatedEvent")
    void updateProductIfFound() {
        UUID existingProductId = UUID.randomUUID();
        Product product = Product.builder()
                .id(existingProductId)
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .build();
        UpdateProductInput updateProductInput = UpdateProductInput.builder()
                .name("Any product name")
                .description("Any change")
                .price(BigDecimal.valueOf(369))
                .build();
        Product expectedUpdatedProduct = Product.builder()
                .id(existingProductId)
                .name("Any product name")
                .description(updateProductInput.getDescription())
                .price(BigDecimal.valueOf(369))
                .build();
        when(productDatabaseGatewayMock.findProductById(existingProductId)).thenReturn(Optional.of(product));
        sut.execute(existingProductId, updateProductInput);
        verify(productDatabaseGatewayMock, times(1)).findProductById(existingProductId);
        verify(modelMapperSpy, times(1)).map(updateProductInput, product);
        verify(productDatabaseGatewayMock, times(1)).saveProduct(expectedUpdatedProduct);
        verify(messagePublisher, times(1)).sendMessage(
                "product_updated",
                "{\"" +
                        "id\": \"" + product.getId() +
                        "name\": \"" + product.getName() +
                        "description\": \"" + product.getDescription() +
                        "price\": \"" + product.getPrice() +
                        "\", \"stockQuantity\": " + updateProductInput.getStockQuantity() +
                        "}"
        );
        verify(productMapperMock, times(1)).mapEntityToDTO(expectedUpdatedProduct);
    }

    @Test
    @DisplayName("It should throw an exception if the product is not found")
    void productNotFound() {
        UUID nonexistentProductId = UUID.randomUUID();
        UpdateProductInput updateProductInput = UpdateProductInput.builder()
                .name("Any product name")
                .description("Any change")
                .price(BigDecimal.valueOf(369))
                .build();
        when(productDatabaseGatewayMock.findProductById(nonexistentProductId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            sut.execute(nonexistentProductId, updateProductInput);
        });
    }
}