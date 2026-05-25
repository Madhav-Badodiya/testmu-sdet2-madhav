package com.testmu.tests.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.testmu.base.BaseTest;
import com.testmu.pages.InventoryPage;
import com.testmu.pages.LoginPage;
import com.testmu.utils.CustomAssertions;
import com.testmu.utils.JsonDataReader;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LoginTest — covers valid login, locked-out user, empty credentials, wrong credentials.
 * Data-driven via login_data.json.
 */
@Epic("UI Tests")
@Feature("Login")
public class LoginTest extends BaseTest {

    // ── DataProviders — from externalized JSON ───────────────────────────────

    @DataProvider(name = "validUsers")
    public Object[][] validUsers() {
        return JsonDataReader.toDataProvider("login_data.json", "valid_users");
    }

    @DataProvider(name = "invalidUsers")
    public Object[][] invalidUsers() {
        return JsonDataReader.toDataProvider("login_data.json", "invalid_users");
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    @Test(dataProvider = "validUsers", description = "Valid user should reach inventory page")
    @Story("Successful Login")
    @Severity(SeverityLevel.BLOCKER)
    public void testValidLogin(JsonNode userData) {
        String username = userData.get("username").asText();
        String password = userData.get("password").asText();
        String expectedTitle = userData.get("expected_title").asText();

        InventoryPage inventoryPage = new LoginPage()
                .open()
                .loginAs(username, password);

        CustomAssertions.assertPageTitle(inventoryPage.getTitle(), "Products");
        CustomAssertions.assertUrlContains(inventoryPage.getCurrentUrl(), "inventory");
    }

    @Test(dataProvider = "invalidUsers", description = "Invalid user should see error message")
    @Story("Failed Login")
    @Severity(SeverityLevel.CRITICAL)
    public void testInvalidLogin(JsonNode userData) {
        String username = userData.get("username").asText();
        String password = userData.get("password").asText();
        String expectedError = userData.get("expected_error").asText();

        LoginPage loginPage = new LoginPage()
                .open()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginExpectingError();

        CustomAssertions.assertErrorMessage(loginPage.getErrorMessage(), expectedError);
        CustomAssertions.assertUrlContains(loginPage.getCurrentUrl(), "saucedemo.com");
    }

    @Test(description = "Login page should load and have expected elements")
    @Story("Login Page Load")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginPageLoads() {
        LoginPage loginPage = new LoginPage().open();

        CustomAssertions.assertUrlContains(loginPage.getCurrentUrl(), "saucedemo.com");
        CustomAssertions.assertPageTitle(loginPage.getPageTitle(), "Swag Labs");
    }
}
