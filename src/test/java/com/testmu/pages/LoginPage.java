package com.testmu.pages;

import com.testmu.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage — SauceDemo login page.
 * All selectors use data-test attributes (stable, not brittle XPath).
 */
public class LoginPage extends BasePage {

    // ── Locators — data-test attributes ─────────────────────────────────────
    @FindBy(css = "[data-test='username']")
    private WebElement usernameInput;

    @FindBy(css = "[data-test='password']")
    private WebElement passwordInput;

    @FindBy(css = "[data-test='login-button']")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // ── Actions ──────────────────────────────────────────────────────────────

    @Step("Navigate to login page")
    public LoginPage open() {
        navigateTo(config.getUIBaseUrl());
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    @Step("Click login button")
    public InventoryPage clickLogin() {
        click(loginButton);
        return new InventoryPage();
    }

    @Step("Login with invalid credentials — expect error")
    public LoginPage clickLoginExpectingError() {
        click(loginButton);
        return this;
    }

    @Step("Login as {username}")
    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    // ── Assertions support ───────────────────────────────────────────────────

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("saucedemo.com");
    }
}
