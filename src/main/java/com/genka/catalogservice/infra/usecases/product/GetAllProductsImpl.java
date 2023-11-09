package com.genka.catalogservice.infra.usecases.product;

import com.genka.catalogservice.application.gateways.product.ProductDatabaseGateway;
import com.genka.catalogservice.application.usecases.product.GetAllProducts;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import com.genka.catalogservice.infra.mappers.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllProductsImpl implements GetAllProducts {
    private final ProductDatabaseGateway productDatabaseGateway;
    private final ProductMapper productMapper;

    public GetAllProductsImpl(ProductDatabaseGateway productDatabaseGateway, ProductMapper productMapper) {
        this.productDatabaseGateway = productDatabaseGateway;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> execute() {
        List<Product> products = this.productDatabaseGateway.findAllProducts();
        return products.stream().map(this.productMapper::mapEntityToDTO).toList();
    }
}
