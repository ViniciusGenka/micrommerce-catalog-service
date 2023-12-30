package com.genka.catalogservice.application.services;

import com.genka.catalogservice.domain.product.Product;

public interface PublishProductUpdatedEventService {
    void execute(Product updatedProduct);
}
