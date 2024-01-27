package com.genka.catalogservice.infra.controllers;

import com.genka.catalogservice.application.controllers.ProductController;
import com.genka.catalogservice.application.usecases.product.AddProduct;
import com.genka.catalogservice.application.usecases.product.GetAllProducts;
import com.genka.catalogservice.application.usecases.product.GetOneProduct;
import com.genka.catalogservice.application.usecases.product.UpdateProduct;
import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalogs")
public class ProductControllerImpl implements ProductController {
    private final AddProduct addProductUseCase;
    private final GetAllProducts getAllProductsUseCase;
    private final GetOneProduct GetOneProductUseCase;
    private final UpdateProduct updateProductUseCase;

    public ProductControllerImpl(AddProduct addProductUseCase, GetAllProducts getAllProductsUseCase, GetOneProduct getOneProduct, UpdateProduct updateProductUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.GetOneProductUseCase = getOneProduct;
        this.updateProductUseCase = updateProductUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductDTO> addProduct(@RequestBody @Valid AddProductInput addProductInput) {
        ProductDTO savedProduct = this.addProductUseCase.execute(addProductInput);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PatchMapping("/{id}")
    @Override
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID id, @RequestBody UpdateProductInput updateProductInput) {
        ProductDTO updatedProduct = this.updateProductUseCase.execute(id, updateProductInput);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProduct);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ProductDTO> getOneProduct(@PathVariable UUID id) {
        ProductDTO product = this.GetOneProductUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.OK).body(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = this.getAllProductsUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }
}