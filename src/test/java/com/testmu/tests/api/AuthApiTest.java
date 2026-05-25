package com.testmu.tests.api;

import com.testmu.base.BaseApiTest;
import com.testmu.utils.CustomAssertions;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * AuthApiTest — token generation, valid and invalid credentials.
 */
@Epic("API Tests")
@Feature("Authentication")
public class AuthApiTest extends BaseApiTest {

    @Test(description = "Valid credentials return auth token")
    @Story("Token Generation")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidCredentialsReturnToken() {
        long startTime = System.currentTimeMillis();

        Response response = given()
                .spec(requestSpec)
                .body("{ \"username\": \"" + config.getApiUsername() +
                      "\", \"password\": \"" + config.getApiPassword() + "\" }")
                .when()
                .post("/auth")
                .then()
                .extract().response();

        long responseTime = System.currentTimeMillis() - startTime;

        CustomAssertions.assertStatusCode(response.statusCode(), 200);
        CustomAssertions.assertResponseTime(responseTime, 5000);

        String token = response.jsonPath().getString("token");
        CustomAssertions.assertFieldNotEmpty("token", token);
    }

    @Test(description = "Invalid credentials return bad credentials message")
    @Story("Invalid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidCredentialsReturnError() {
        Response response = given()
                .spec(requestSpec)
                .body("{ \"username\": \"wronguser\", \"password\": \"wrongpass\" }")
                .when()
                .post("/auth")
                .then()
                .extract().response();

        CustomAssertions.assertStatusCode(response.statusCode(), 200);
        // Restful-Booker returns 200 with "Bad credentials" in body for invalid auth
        String reason = response.jsonPath().getString("reason");
        Assert.assertEquals(reason, "Bad credentials",
                "Invalid credentials should return 'Bad credentials' reason");
    }
}
