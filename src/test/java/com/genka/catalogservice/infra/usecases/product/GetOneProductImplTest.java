package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOneProductImplTest {
    @Mock
    private ProductDatabaseGateway productDatabaseGatewayMock;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private GetOneProductImpl sut;

    @Test
    @DisplayName("It should call the 'findProductById' method from ProductDatabaseGateway and then return a mapped ProductDTO")
    void getMappedProductIfFound() {
        UUID existingProductId = UUID.randomUUID();
        Product product = Product.builder()
                .id(existingProductId)
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .build();
        when(productDatabaseGatewayMock.findProductById(existingProductId)).thenReturn(Optional.of(product));
        sut.execute(existingProductId);
        verify(productDatabaseGatewayMock, times(1)).findProductById(existingProductId);
        verify(productMapper, times(1)).mapEntityToDTO(product);
    }

    @Test
    @DisplayName("It should throw an exception if the product is not found")
    void productNotFound() {
        UUID nonexistentProductId = UUID.randomUUID();
        when(productDatabaseGatewayMock.findProductById(nonexistentProductId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            sut.execute(nonexistentProductId);
        });
    }
}