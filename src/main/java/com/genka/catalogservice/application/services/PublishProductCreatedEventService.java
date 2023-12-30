package com.genka.catalogservice.application.services;

import com.genka.catalogservice.domain.product.Product;

public interface PublishProductCreatedEventService {
    void execute(Product createdProduct);
}
