package com.genka.catalogservice.application.messaging;

public interface MessagePublisher {
    void sendMessage(String channel, String message);
}
