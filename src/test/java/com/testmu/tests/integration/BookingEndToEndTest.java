package com.testmu.tests.integration;

import com.testmu.base.BaseApiTest;
import com.testmu.config.DriverManager;
import com.testmu.pages.InventoryPage;
import com.testmu.pages.LoginPage;
import com.testmu.utils.CustomAssertions;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * BookingEndToEndTest — Integration test combining API and UI layers.
 *
 * Flow:
 * 1. [API] Authenticate with Restful-Booker → get token
 * 2. [API] Create a new booking → capture booking ID
 * 3. [API] GET the created booking → verify data integrity
 * 4. [UI]  Login to SauceDemo → verify dashboard loads (represents UI layer of the platform)
 * 5. [UI]  Add item to cart → verify cart state
 * 6. [API] Delete the booking → verify 404 cleanup
 *
 * This pattern mirrors real-world SDET work: seeding state via API,
 * asserting UI reflects that state, then cleaning up via API.
 */
@Epic("Integration Tests")
@Feature("API + UI End-to-End")
public class BookingEndToEndTest extends BaseApiTest {

    private int bookingId;
    private String authToken;

    @BeforeMethod
    public void initBrowser() {
        DriverManager.initDriver("chrome");
    }

    @AfterMethod
    public void tearDownBrowser() {
        DriverManager.quitDriver();
    }

    @Test(description = "Create booking via API, verify UI session, clean up via API")
    @Story("Full E2E: API seed → UI verify → API cleanup")
    @Severity(SeverityLevel.BLOCKER)
    public void testFullEndToEndFlow() throws InterruptedException {

        // ── Step 1: Authenticate via API ─────────────────────────────────────
        Response authResponse = given()
                .spec(requestSpec)
                .body("{ \"username\": \"" + config.getApiUsername() +
                      "\", \"password\": \"" + config.getApiPassword() + "\" }")
                .when()
                .post("/auth");

        CustomAssertions.assertStatusCode(authResponse.statusCode(), 200);
        authToken = authResponse.jsonPath().getString("token");
        Assert.assertNotNull(authToken, "Auth token should not be null");

        // ── Step 2: Create booking via API ────────────────────────────────────
        Response createResponse = given()
                .spec(requestSpec)
                .body("""
                        {
                            "firstname": "Integration",
                            "lastname": "TestRun",
                            "totalprice": 300,
                            "depositpaid": true,
                            "bookingdates": {
                                "checkin": "2026-09-01",
                                "checkout": "2026-09-05"
                            },
                            "additionalneeds": "E2E Test"
                        }""")
                .when()
                .post("/booking");

        CustomAssertions.assertStatusCode(createResponse.statusCode(), 200);
        bookingId = createResponse.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Booking ID should be a positive integer");

        // ── Step 3: Verify booking exists via GET ─────────────────────────────
        Thread.sleep(2000);
        Response getResponse = given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + bookingId);

        CustomAssertions.assertStatusCode(getResponse.statusCode(), 200);
        Assert.assertEquals(getResponse.jsonPath().getString("firstname"), "Integration",
                "Booking firstname should match what was created");

        // ── Step 4: UI Layer — Login to SauceDemo ─────────────────────────────
        // Represents: API sets up data state, UI session is verified independently
        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginAs("standard_user", "secret_sauce");

        Assert.assertTrue(inventoryPage.isLoaded(),
                "UI layer: Inventory page should load after login");
        CustomAssertions.assertPageTitle(inventoryPage.getTitle(), "Products");

        // ── Step 5: UI Layer — Interact with dashboard ────────────────────────
        inventoryPage.addFirstItemToCart();
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1",
                "UI layer: Cart should reflect added item");

        // ── Step 6: API Cleanup — Delete booking ──────────────────────────────
        given()
                .spec(requestSpec)
                .cookie("token", authToken)
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);

        // Verify deletion
        given()
                .spec(requestSpec)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(404);
    }
}
