package com.company.automation.core;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import com.company.automation.config.SecretManager;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;

/**
 * Central REST Assured request configuration.
 *
 * Shared headers, content type, logging, and Allure attachment behavior belong
 * here so individual API clients only describe endpoint-specific behavior.
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
        // Static factory utility.
    }

    public static RequestSpecification getRequestSpec(String baseUrl) {

        String username = SecretManager.get("TODO_API_USERNAME");
        String password = SecretManager.get("TODO_API_PASSWORD");

        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())
                .setBaseUri(baseUrl)
                .setContentType("application/json")
                .addHeader("X-Test-Client", "practice-java")
                .setAuth(RestAssured.preemptive().basic(username, password))
                .setConfig(
                        RestAssured.config()
                                .logConfig(LogConfig.logConfig()
                                        .blacklistHeader("Authorization")))
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .log(LogDetail.HEADERS)
                .log(LogDetail.BODY)
                .build();
    }
}