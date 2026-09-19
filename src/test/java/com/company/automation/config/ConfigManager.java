package com.company.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();

    private static final String environment = System.getProperty("env", "qa");

    static {
        String configFile = "config-" + environment + ".properties";

        System.out.println("ENVIRONMENT = " + environment);
        System.out.println("CONFIG FILE = " + configFile);

        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(configFile)) {

            if (input == null) {
                throw new RuntimeException(
                        "Config file not found: " + configFile);
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config file: " + configFile,
                    e);
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException(
                    "Property not found: " + key);
        }

        return value;
    }

    public static String getEnvironment() {
        return environment;
    }
}