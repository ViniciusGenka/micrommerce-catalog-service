package com.genka.catalogservice.infra.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genka.catalogservice.application.messaging.MessagePublisher;
import com.genka.catalogservice.application.messaging.dtos.ProductCreatedEvent;
import com.genka.catalogservice.application.messaging.dtos.ProductInventoryUpdatedEvent;
import com.genka.catalogservice.application.services.PublishProductInventoryUpdatedEventService;
import com.genka.catalogservice.application.services.PublishProductUpdatedEventService;
import com.genka.catalogservice.domain.product.Product;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PublishProductInventoryUpdatedEventServiceImpl implements PublishProductInventoryUpdatedEventService {

    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public PublishProductInventoryUpdatedEventServiceImpl(MessagePublisher messagePublisher, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(UUID productId, Integer stockQuantity) {
        try {
            ProductInventoryUpdatedEvent productInventoryUpdatedEvent = ProductInventoryUpdatedEvent.builder()
                    .productId(productId)
                    .stockQuantity(stockQuantity)
                    .build();
            this.messagePublisher.sendMessage(
                    "product_inventory_updated",
                    objectMapper.writeValueAsString(productInventoryUpdatedEvent)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}