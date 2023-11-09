package com.genka.catalogservice.infra.mappers;

import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO mapEntityToDTO(Product product) {
        return ProductDTO.mapFromEntity(product);
    }
}
