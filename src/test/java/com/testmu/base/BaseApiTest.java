package com.testmu.base;

import com.testmu.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

/**
 * BaseApiTest — parent for all API test classes.
 * Centralises base URL, content type, logging, and Allure filter.
 */
public class BaseApiTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected static ConfigManager config = ConfigManager.getInstance();

    @BeforeClass
    public void setupApi() {
        RestAssured.baseURI = config.getAPIBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getAPIBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .log(LogDetail.ALL)
                .build();
    }
}
