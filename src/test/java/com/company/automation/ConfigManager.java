package com.company.automation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigManager {

    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties was not found on the test classpath");
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        String env = System.getProperty("env", "qa");
        return get("base.url." + env);
    }
}
