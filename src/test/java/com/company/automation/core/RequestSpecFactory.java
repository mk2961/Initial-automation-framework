package com.company.automation.core;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

    public static RequestSpecification getRequestSpec(String baseUrl) {


        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder.addFilter(new AllureRestAssured());
        requestSpecBuilder.setBaseUri(baseUrl);
        requestSpecBuilder.setContentType("application/json");
        requestSpecBuilder.addHeader("X-Test-Client", "practice-java");

        requestSpecBuilder.log(LogDetail.METHOD);
        requestSpecBuilder.log(LogDetail.URI);
        requestSpecBuilder.log(LogDetail.HEADERS);
        requestSpecBuilder.log(LogDetail.BODY);

        RequestSpecification requestSpecification =
                requestSpecBuilder.build();

        return requestSpecification;
    }
}