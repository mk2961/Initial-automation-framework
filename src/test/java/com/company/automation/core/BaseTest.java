package com.company.automation.core;

import com.company.automation.config.ConfigManager;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void printEnvironment() {
        System.out.println();
        System.out.println("====================================");
        System.out.println("TEST ENVIRONMENT: "
                + ConfigManager.getEnvironment().toUpperCase());
        System.out.println("====================================");
        System.out.println();
    }
}