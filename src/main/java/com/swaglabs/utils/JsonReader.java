package com.swaglabs.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.StreamSupport;

public final class JsonReader {
    private final JsonNode data;

    public JsonReader(String resourcePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream input = getResource(resourcePath)) {
            data = objectMapper.readTree(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read JSON resource: " + resourcePath, exception);
        }
    }

    public String getString(String key) {
        JsonNode value = requiredValue(key);
        if (!value.isTextual()) {
            throw new IllegalArgumentException("JSON value is not text: " + key);
        }
        return value.asText();
    }

    public int getInt(String key) {
        JsonNode value = requiredValue(key);
        if (!value.isInt()) {
            throw new IllegalArgumentException("JSON value is not an integer: " + key);
        }
        return value.asInt();
    }

    public List<String> getStringList(String key) {
        JsonNode value = requiredValue(key);
        if (!value.isArray()) {
            throw new IllegalArgumentException("JSON value is not an array: " + key);
        }
        return StreamSupport.stream(value.spliterator(), false)
                .map(item -> {
                    if (!item.isTextual()) {
                        throw new IllegalArgumentException(
                                "JSON array contains a non-text value: " + key);
                    }
                    return item.asText();
                })
                .toList();
    }

    private JsonNode requiredValue(String key) {
        JsonNode value = data.get(key);
        if (value == null || value.isNull()) {
            throw new IllegalArgumentException("Missing JSON value: " + key);
        }
        return value;
    }

    private InputStream getResource(String path) {
        InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path);
        if (input == null) {
            throw new IllegalStateException("Resource not found: " + path);
        }
        return input;
    }
}

