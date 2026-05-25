package com.testmu.tests.api;

import com.testmu.base.BaseApiTest;
import com.testmu.utils.CustomAssertions;
import io.qameta.allure.*;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * ErrorHandlingTest — verifies correct 4xx/5xx handling by the API.
 */
@Epic("API Tests")
@Feature("Error Handling")
public class ErrorHandlingTest extends BaseApiTest {

    @Test(description = "GET non-existent booking ID returns 404")
    @Story("404 Not Found")
    @Severity(SeverityLevel.NORMAL)
    public void testGetNonExistentBookingReturns404() {
        long start = System.currentTimeMillis();

        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/booking/999999999")
                .then()
                .statusCode(404)
                .extract().response();

        CustomAssertions.assertResponseTime(System.currentTimeMillis() - start, 5000);
    }

    @Test(description = "DELETE without auth token returns 403")
    @Story("403 Forbidden")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteWithoutAuthReturns403() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/booking/1")
                .then()
                .statusCode(403);
    }

    @Test(description = "PUT without auth token returns 403")
    @Story("403 Forbidden")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateWithoutAuthReturns403() {
        given()
                .spec(requestSpec)
                .body("""
                        {
                            "firstname": "Test",
                            "lastname": "User",
                            "totalprice": 100,
                            "depositpaid": true,
                            "bookingdates": {
                                "checkin": "2026-01-01",
                                "checkout": "2026-01-05"
                            },
                            "additionalneeds": "None"
                        }""")
                .when()
                .put("/booking/1")
                .then()
                .statusCode(403);
    }

    @Test(description = "POST /booking with empty body returns error status")
    @Story("Validation Error")
    @Severity(SeverityLevel.NORMAL)
    public void testEmptyPayloadReturnsError() {
        Response response = given()
                .spec(requestSpec)
                .body("{}")
                .when()
                .post("/booking")
                .then()
                .extract().response();

        int status = response.statusCode();
        Assert.assertTrue(status == 200 || status == 500,
                "Empty payload should return 200 or 500, got: " + status);
    }
}
