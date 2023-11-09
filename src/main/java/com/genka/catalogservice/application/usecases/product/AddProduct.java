package com.genka.catalogservice.application.usecases.product;

import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;

public interface AddProduct {
    void execute(AddProductInput addProductInput);
}
