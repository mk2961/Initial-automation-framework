package com.company.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads environment-specific framework configuration from
 * config-{env}.properties. The environment is selected with -Denv=<name> and
 * defaults to qa when no JVM property is supplied.
 *
 * Example: mvn test "-Denv=local"
 */
public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();
    private static final String ENVIRONMENT = System.getProperty("env", "qa");

    static {
        String configFile = "config-" + ENVIRONMENT + ".properties";

        System.out.println("ENVIRONMENT = " + ENVIRONMENT);
        System.out.println("CONFIG FILE = " + configFile);

        try (InputStream input = ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(configFile)) {

            if (input == null) {
                throw new RuntimeException("Config file not found: " + configFile);
            }

            PROPERTIES.load(input);

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to load config file: " + configFile,
                    exception);
        }
    }

    private ConfigManager() {
        // Static configuration utility.
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }

        return value;
    }

    public static String getEnvironment() {
        return ENVIRONMENT;
    }
}
