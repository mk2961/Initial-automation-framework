package com.company.automation.core;

import org.junit.jupiter.api.BeforeAll;

import com.company.automation.config.ConfigManager;

/**
 * Common base behavior for framework tests.
 *
 * Printing the selected environment once per test class makes accidental runs
 * against the wrong target easier to spot in local and CI logs.
 */
public abstract class BaseTest {

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
