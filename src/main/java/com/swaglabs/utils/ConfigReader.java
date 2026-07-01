package com.swaglabs.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private final Properties properties = new Properties();

    public ConfigReader() {
        try (InputStream input = getResource(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + CONFIG_FILE, exception);
        }
    }

    public String get(String key) {
        String value = System.getProperty(key, properties.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    public boolean getBoolean(String key) {
        String value = get(key);
        if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
            throw new IllegalArgumentException(
                    "Configuration property must be true or false: " + key);
        }
        return Boolean.parseBoolean(value);
    }

    public long getLong(String key) {
        return Long.parseLong(get(key));
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
