package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.messaging.MessagePublisher;
import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductImplTest {

    @Mock
    private ProductDatabaseGateway productDatabaseGatewayMock;
    @Mock
    private MessagePublisher messagePublisherMock;
    @Mock
    private ProductMapper productMapperMock;
    @InjectMocks
    private AddProductImpl sut;

    @Test
    @DisplayName("It should call the 'saveProduct' method from ProductDatabaseGateway")
    void saveProduct() {
        AddProductInput input = AddProductInput.builder()
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .stockQuantity(369)
                .build();
        Product productToSave = Product.builder()
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .build();
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .build();
        when(productDatabaseGatewayMock.saveProduct(productToSave)).thenReturn(savedProduct);
        sut.execute(input);
        verify(productDatabaseGatewayMock, times(1)).saveProduct(productToSave);
        verify(messagePublisherMock, times(1)).sendMessage(
                "product_created",
                "{\"productId\": \"" + savedProduct.getId() + "\", \"stockQuantity\": " + input.getStockQuantity() + "}"
        );
        verify(productMapperMock, times(1)).mapEntityToDTO(savedProduct);
    }

}