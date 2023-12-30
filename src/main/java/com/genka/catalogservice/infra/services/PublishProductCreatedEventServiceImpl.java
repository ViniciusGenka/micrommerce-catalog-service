package com.genka.catalogservice.infra.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genka.catalogservice.application.messaging.MessagePublisher;
import com.genka.catalogservice.application.messaging.dtos.ProductCreatedEvent;
import com.genka.catalogservice.application.services.PublishProductCreatedEventService;
import com.genka.catalogservice.domain.product.Product;
import org.springframework.stereotype.Service;

@Service
public class PublishProductCreatedEventServiceImpl implements PublishProductCreatedEventService {

    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public PublishProductCreatedEventServiceImpl(MessagePublisher messagePublisher, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(Product createdProduct) {
        try {
            ProductCreatedEvent createdProductEvent = ProductCreatedEvent.mapFromEntity(createdProduct);
            this.messagePublisher.sendMessage(
                    "product_created",
                    objectMapper.writeValueAsString(createdProductEvent)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}