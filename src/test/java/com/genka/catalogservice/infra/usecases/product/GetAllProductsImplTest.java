package com.genka.catalogservice.infra.usecases.product;

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
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllProductsImplTest {
    @Mock
    private ProductDatabaseGateway productDatabaseGatewayMock;

    @Mock
    private ProductMapper productMapperMock;

    @InjectMocks
    private GetAllProductsImpl sut;

    @Test
    @DisplayName("It should call the 'findAllProducts' method from ProductDatabaseGateway and then map the products to ProductDTOs")
    void getMappedProductsIfFound() {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Any product name")
                .description("Any product description")
                .price(BigDecimal.valueOf(369))
                .build();
        when(productDatabaseGatewayMock.findAllProducts()).thenReturn(Collections.singletonList(product));
        sut.execute();
        verify(productDatabaseGatewayMock, times(1)).findAllProducts();
        verify(productMapperMock, times(1)).mapEntityToDTO(product);
    }
}