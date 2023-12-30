package com.genka.catalogservice.infra.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genka.catalogservice.application.messaging.MessagePublisher;
import com.genka.catalogservice.application.messaging.dtos.ProductCreatedEvent;
import com.genka.catalogservice.application.services.PublishProductCreatedEventService;
import com.genka.catalogservice.application.services.PublishProductUpdatedEventService;
import com.genka.catalogservice.domain.product.Product;
import org.springframework.stereotype.Service;

@Service
public class PublishProductUpdatedEventServiceImpl implements PublishProductUpdatedEventService {

    private final MessagePublisher messagePublisher;
    private final ObjectMapper objectMapper;

    public PublishProductUpdatedEventServiceImpl(MessagePublisher messagePublisher, ObjectMapper objectMapper) {
        this.messagePublisher = messagePublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(Product updatedProduct) {
        try {
            ProductCreatedEvent productUpdatedEvent = ProductCreatedEvent.mapFromEntity(updatedProduct);
            this.messagePublisher.sendMessage(
                    "product_updated",
                    objectMapper.writeValueAsString(productUpdatedEvent)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}