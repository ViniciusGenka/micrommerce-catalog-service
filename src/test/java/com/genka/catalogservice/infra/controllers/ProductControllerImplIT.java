package com.genka.catalogservice.infra.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genka.catalogservice.application.exceptions.EntityNotFoundException;
import com.genka.catalogservice.application.messaging.dtos.ProductCreatedEvent;
import com.genka.catalogservice.application.messaging.dtos.ProductInventoryUpdatedEvent;
import com.genka.catalogservice.application.messaging.dtos.ProductUpdatedEvent;
import com.genka.catalogservice.application.usecases.product.dtos.AddProductInput;
import com.genka.catalogservice.application.usecases.product.dtos.UpdateProductInput;
import com.genka.catalogservice.domain.product.Product;
import com.genka.catalogservice.domain.product.dtos.ProductDTO;
import com.genka.catalogservice.infra.repositories.product.mongodb.ProductDatabaseGatewayMongodb;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static org.testcontainers.shaded.org.awaitility.Durations.ONE_MINUTE;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerImplIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ProductDatabaseGatewayMongodb productDatabaseGatewayMongodb;
    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    @Container
    @ServiceConnection
    public static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:latest"));
    @Container
    @ServiceConnection
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    private ProductCreatedEvent publishedProductCreatedEvent;
    private ProductUpdatedEvent publishedProductUpdatedEvent;
    private ProductInventoryUpdatedEvent publishedProductInventoryUpdatedEvent;

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void addProductEndpoint() {
        AddProductInput addProductInput = AddProductInput.builder()
                .name("Product name")
                .description("Product description")
                .price(BigDecimal.valueOf(100))
                .stockQuantity(1)
                .build();
        Product expectedProduct = Product.builder()
                .name("Product name")
                .description("Product description")
                .price(BigDecimal.valueOf(100))
                .build();
        ProductDTO expectedProductDTO = ProductDTO.mapFromEntity(expectedProduct);
        ResponseEntity<ProductDTO> addedProductDTOResponseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/catalogs",
                addProductInput,
                ProductDTO.class
        );
        UUID addedProductId = Objects.requireNonNull(addedProductDTOResponseEntity.getBody()).getId();
        Product savedProduct = productDatabaseGatewayMongodb.findProductById(addedProductId).orElseThrow(() -> new EntityNotFoundException("Product with id" + addedProductId + " not found"));
        ProductCreatedEvent expectedProductCreatedEvent = ProductCreatedEvent.mapFromEntity(savedProduct);
        //Assert that no error was thrown
        assertEquals(HttpStatus.CREATED, addedProductDTOResponseEntity.getStatusCode());
        //Compare the Product saved in the database with the expected Product
        assertThat(savedProduct)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedProductDTO);
        //Compare the placed OrderDTO response with the expected OrderDTO
        assertNotNull(addedProductDTOResponseEntity.getBody());
        assertThat(addedProductDTOResponseEntity.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedProductDTO);
        //Compare the published OrderPlacedEvent with the expected OrderPlacedEvent
        await().atMost(ONE_MINUTE).untilAsserted(() -> {
            assertThat(publishedProductCreatedEvent)
                    .isEqualTo(expectedProductCreatedEvent);
        });
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void updateProductEndpoint() {
        Product product = Product.builder()
                .name("Product name")
                .description("Product description")
                .price(BigDecimal.valueOf(200))
                .build();
        Product savedProduct = this.productDatabaseGatewayMongodb.saveProduct(product);
        UpdateProductInput updateProductInput = UpdateProductInput.builder()
                .description("Any description change")
                .stockQuantity(100)
                .build();
        Product expectedUpdatedProduct = Product.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(updateProductInput.getDescription())
                .price(savedProduct.getPrice())
                .build();
        ProductUpdatedEvent expectedProductUpdatedEvent = ProductUpdatedEvent.mapFromEntity(expectedUpdatedProduct);
        ProductInventoryUpdatedEvent productInventoryUpdatedEvent = ProductInventoryUpdatedEvent.builder()
                .productId(product.getId())
                .stockQuantity(updateProductInput.getStockQuantity())
                .build();
        ProductDTO expectedUpdatedProductDTO = ProductDTO.mapFromEntity(expectedUpdatedProduct);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ProductDTO> updatedProductDTOResponseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/catalogs/" + savedProduct.getId(),
                HttpMethod.PATCH,
                new HttpEntity<>(updateProductInput, headers),
                ProductDTO.class
        );
        assertNotNull(updatedProductDTOResponseEntity.getBody());
        assertThat(updatedProductDTOResponseEntity.getBody())
                .isEqualTo(expectedUpdatedProductDTO);
        await().atMost(ONE_MINUTE).untilAsserted(() -> {
            assertThat(publishedProductUpdatedEvent)
                    .isEqualTo(expectedProductUpdatedEvent);
            assertThat(publishedProductInventoryUpdatedEvent)
                    .isEqualTo(productInventoryUpdatedEvent);
        });
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void getOneProductEndpoint() {
        Product product = Product.builder()
                .name("Product name")
                .description("Product description")
                .price(BigDecimal.valueOf(400))
                .build();
        Product savedProduct = this.productDatabaseGatewayMongodb.saveProduct(product);
        ProductDTO expectedProductDTO = ProductDTO.mapFromEntity(savedProduct);
        ResponseEntity<ProductDTO> productDTOResponseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/catalogs/" + savedProduct.getId(),
                ProductDTO.class
        );
        assertNotNull(productDTOResponseEntity.getBody());
        assertThat(productDTOResponseEntity.getBody())
                .isEqualTo(expectedProductDTO);
    }

    @Test
    @DirtiesContext(methodMode = BEFORE_METHOD)
    public void getAllProductsEndpoint() {
        Product productToSave1 = Product.builder()
                .name("Product 1")
                .description("Product description 1")
                .price(BigDecimal.valueOf(500))
                .build();
        Product productToSave2 = Product.builder()
                .name("Product 2")
                .description("Product description 2")
                .price(BigDecimal.valueOf(600))
                .build();
        Product savedProduct1 = this.productDatabaseGatewayMongodb.saveProduct(productToSave1);
        Product savedProduct2 = this.productDatabaseGatewayMongodb.saveProduct(productToSave2);
        ProductDTO expectedProductDTO1 = ProductDTO.mapFromEntity(savedProduct1);
        ProductDTO expectedProductDTO2 = ProductDTO.mapFromEntity(savedProduct2);
        List<ProductDTO> expectedProductDTOsList = List.of(expectedProductDTO1, expectedProductDTO2);
        ResponseEntity<List<ProductDTO>> productDTOsResponseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/catalogs",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertNotNull(productDTOsResponseEntity.getBody());
        assertFalse(productDTOsResponseEntity.getBody().isEmpty());
        assertThat(productDTOsResponseEntity.getBody())
                .isEqualTo(expectedProductDTOsList);
    }

    @KafkaListener(topics = "product_created", groupId = "test-group")
    public void listenProductCreatedEvent(String message) throws JsonProcessingException {
        this.publishedProductCreatedEvent = mapper.readValue(message, ProductCreatedEvent.class);
    }

    @KafkaListener(topics = "product_updated", groupId = "test-group")
    public void listenProductUpdatedEvent(String message) throws JsonProcessingException {
        this.publishedProductUpdatedEvent = mapper.readValue(message, ProductUpdatedEvent.class);
    }

    @KafkaListener(topics = "product_inventory_updated", groupId = "test-group")
    public void listenProductInventoryUpdatedEvent(String message) throws JsonProcessingException {
        this.publishedProductInventoryUpdatedEvent = mapper.readValue(message, ProductInventoryUpdatedEvent.class);
    }
}