package com.genka.catalogservice.infra.configs.mongodb;

import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.UUID;

@Component
public class MongodbBinaryToUUIDConverter implements Converter<Binary, UUID> {
    @Override
    public UUID convert(Binary binary) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(binary.getData());
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
