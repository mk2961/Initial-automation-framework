package com.company.automation.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class SecretManager {

    private static final Map<String, String> secrets = new HashMap<>();

    static {
        Path envFile = Path.of(".env");

        if (!Files.exists(envFile)) {
            throw new RuntimeException(
                    ".env file not found. Local secrets are required.");
        }

        try {
            Files.lines(envFile)
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);

                        if (parts.length == 2) {
                            secrets.put(
                                    parts[0].trim(),
                                    parts[1].trim());
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load local secrets from .env", e);
        }
    }

    private SecretManager() {
        // Static utility class.
    }

    public static String get(String key) {

        String value = secrets.get(key);
        if (value == null) {
            throw new RuntimeException(
                    "Required secret not found: " + key);
        }

        return value;
    }
}