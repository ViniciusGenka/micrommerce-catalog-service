package com.genka.catalogservice.application.services;

import java.util.UUID;

public interface PublishProductInventoryUpdatedEventService {
    void execute(UUID productId, Integer stockQuantity);
}
