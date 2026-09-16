package com.company.automation;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;


public class RequestSpecFactory {

    public static RequestSpecification getRequestSpec() {

        RequestSpecBuilder requestSpecBuilder = 
            new RequestSpecBuilder();
        requestSpecBuilder.addFilter(new AllureRestAssured());
        requestSpecBuilder.setBaseUri(ConfigManager.getBaseUrl());
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
