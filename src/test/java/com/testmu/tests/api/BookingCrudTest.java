package com.testmu.tests.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.testmu.base.BaseApiTest;
import com.testmu.utils.CustomAssertions;
import com.testmu.utils.JsonDataReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * BookingCrudTest — GET all, GET by ID, POST create, PUT update, DELETE.
 * Schema validation on GET. Response-time assertions on all calls.
 */
@Epic("API Tests")
@Feature("Booking CRUD")
public class BookingCrudTest extends BaseApiTest {

    private String authToken;
    private int createdBookingId;
    private JsonNode bookingData;

    @BeforeClass
    public void getToken() {
        super.setupApi();
        bookingData = JsonDataReader.read("booking_data.json");

        Response response = given()
                .spec(requestSpec)
                .body("{ \"username\": \"" + config.getApiUsername() +
                      "\", \"password\": \"" + config.getApiPassword() + "\" }")
                .when()
                .post("/auth");

        authToken = response.jsonPath().getString("token");
        Assert.assertNotNull(authToken, "Auth token must not be null for CRUD tests");
    }

    @Test(priority = 1, description = "GET /booking returns list of booking IDs")
    @Story("Get All Bookings")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllBookings() {
        long start = System.currentTimeMillis();

        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        CustomAssertions.assertResponseTime(System.currentTimeMillis() - start, 5000);
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0,
                "Booking list should not be empty");
    }

    @Test(priority = 2, description = "POST /booking creates a new booking and returns booking ID")
    @Story("Create Booking")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateBooking() {
        JsonNode booking = bookingData.get("bookings").get(0);

        String requestBody = String.format("""
                {
                    "firstname": "%s",
                    "lastname": "%s",
                    "totalprice": %d,
                    "depositpaid": %b,
                    "bookingdates": {
                        "checkin": "%s",
                        "checkout": "%s"
                    },
                    "additionalneeds": "%s"
                }""",
                booking.get("firstname").asText(),
                booking.get("lastname").asText(),
                booking.get("totalprice").asInt(),
                booking.get("depositpaid").asBoolean(),
                booking.get("bookingdates").get("checkin").asText(),
                booking.get("bookingdates").get("checkout").asText(),
                booking.get("additionalneeds").asText());

        long start = System.currentTimeMillis();

        Response response = given()
                .spec(requestSpec)
                .body(requestBody)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        CustomAssertions.assertResponseTime(System.currentTimeMillis() - start, 5000);

        createdBookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(createdBookingId > 0, "Created booking ID should be positive");
        Assert.assertEquals(response.jsonPath().getString("booking.firstname"),
                booking.get("firstname").asText(), "First name should match");
    }

    @Test(priority = 3, description = "GET /booking/{id} returns correct booking with schema validation",
          dependsOnMethods = "testCreateBooking")
    @Story("Get Booking by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookingById() {
        long start = System.currentTimeMillis();

        Response response = given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + createdBookingId)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("testdata/booking_schema.json"))
                .extract().response();

        CustomAssertions.assertResponseTime(System.currentTimeMillis() - start, 5000);
        CustomAssertions.assertFieldNotEmpty("firstname", response.jsonPath().getString("firstname"));
        CustomAssertions.assertFieldNotEmpty("lastname", response.jsonPath().getString("lastname"));
    }

    @Test(priority = 4, description = "PUT /booking/{id} updates existing booking",
          dependsOnMethods = "testCreateBooking")
    @Story("Update Booking")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateBooking() {
        JsonNode update = bookingData.get("update_payload");

        String updateBody = String.format("""
                {
                    "firstname": "%s",
                    "lastname": "%s",
                    "totalprice": %d,
                    "depositpaid": %b,
                    "bookingdates": {
                        "checkin": "%s",
                        "checkout": "%s"
                    },
                    "additionalneeds": "%s"
                }""",
                update.get("firstname").asText(),
                update.get("lastname").asText(),
                update.get("totalprice").asInt(),
                update.get("depositpaid").asBoolean(),
                update.get("bookingdates").get("checkin").asText(),
                update.get("bookingdates").get("checkout").asText(),
                update.get("additionalneeds").asText());

        Response response = given()
                .spec(requestSpec)
                .cookie("token", authToken)
                .body(updateBody)
                .when()
                .put("/booking/" + createdBookingId)
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.jsonPath().getString("firstname"),
                update.get("firstname").asText(), "First name should be updated");
    }

    @Test(priority = 5, description = "DELETE /booking/{id} removes booking, subsequent GET returns 404",
          dependsOnMethods = "testCreateBooking")
    @Story("Delete Booking")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBooking() {
        given()
                .spec(requestSpec)
                .cookie("token", authToken)
                .when()
                .delete("/booking/" + createdBookingId)
                .then()
                .statusCode(201); // Restful-Booker returns 201 on successful delete

        // Verify booking is gone — expect 404
        given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + createdBookingId)
                .then()
                .statusCode(404);
    }
}
